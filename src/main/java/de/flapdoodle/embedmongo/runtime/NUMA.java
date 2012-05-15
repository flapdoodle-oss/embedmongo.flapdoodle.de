/**
 * Copyright (C) 2011
 *   Michael Mosmann <michael@mosmann.de>
 *   Martin Jöhren <m.joehren@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.embedmongo.runtime;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.flapdoodle.embedmongo.collections.Collections;
import de.flapdoodle.embedmongo.distribution.Platform;
import de.flapdoodle.embedmongo.io.StreamCapturer;
import de.flapdoodle.embedmongo.io.StreamGobbler;


public class NUMA {
	
	private static final Logger _logger = Logger.getLogger(NUMA.class.getName());

	static final Map<Platform, Boolean> _numaStatusMap=new HashMap<Platform, Boolean>();
	
	public synchronized static boolean isNUMA(Platform platform) {
		Boolean ret=_numaStatusMap.get(platform);
		if (ret==null) {
			ret=isNUMAOnce(platform);
			_numaStatusMap.put(platform, ret);
		}
		return ret;
	}
	
	public static boolean isNUMAOnce(Platform platform) {
		if (platform==Platform.Linux) {
			try {
				ProcessBuilder processBuilder = new ProcessBuilder(Collections.newArrayList("grep","NUMA=y","/boot/config-`uname -r`"));
				Process process = processBuilder.start();
				StreamGobbler err = new StreamGobbler("grep err", process.getErrorStream());
				StreamCapturer std = new StreamCapturer("grep std", process.getInputStream());
				ProcessShutdownWatcher t = new ProcessShutdownWatcher(process);
				t.start();
				err.start();
				std.start();
				t.join(5000);
				err.join(5000);
				std.join(5000);
				process.destroy();
				String contents = std.getContents();
				boolean isNUMA = !contents.isEmpty();
				if (isNUMA) {
					_logger.warning(
					"-----------------------------------------------\n"+
					"NUMA support is still alpha. If you have any Problems with it, please contact us.\n"+
					"-----------------------------------------------");
				}
				return isNUMA;
//				if (new File("/usr/bin/numactl").exists()) {
//					return true;
//				}
			} catch (IOException ix) {
				_logger.log(Level.SEVERE, "Could not execute grep command: " + ix, ix);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
}
