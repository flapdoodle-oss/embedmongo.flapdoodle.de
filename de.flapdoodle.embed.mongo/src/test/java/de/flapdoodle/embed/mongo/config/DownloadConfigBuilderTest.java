/**
 * Copyright (C) 2011
 *   Michael Mosmann <michael@mosmann.de>
 *   Martin Jöhren <m.joehren@googlemail.com>
 *
 * with contributions from
 * 	konstantin-ba@github,Archimedes Trajano	(trajano@github)
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
package de.flapdoodle.embed.mongo.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import de.flapdoodle.embed.process.io.directories.UserHome;

@RunWith(Parameterized.class)
public class DownloadConfigBuilderTest extends DownloadConfigBuilder {
	private final String description;
	private final String expectedDirectory;

	@Parameterized.Parameters
	public static Collection<Object[]> data() {
		String defaultArtifactDownloadLocation = new UserHome(".embedmongo").asFile().getAbsolutePath();
		return Arrays.asList(new Object[][] {
				{ "Use home directory when environment variable not present", defaultArtifactDownloadLocation, Optional.empty()},
				{ "Environment variable overrides default when supplied", "/some/explicit/directory", Optional.of("/some/explicit/directory") },
				{ "Environment variable overrides default when supplied", "/another/explicit/directory", Optional.of("/another/explicit/directory") },
		});
	}

	public DownloadConfigBuilderTest(String description, String expectedDirectory, Optional<String> artifactDownloadLocationEnvironmentVariable) {
		super(artifactDownloadLocationEnvironmentVariable);
		this.description = description;
		this.expectedDirectory = expectedDirectory;
	}

	@Test
	public void artifactStorePathChosenProperly() {
		defaults();
		Assert.assertEquals(description, expectedDirectory, this.artifactStorePath().get().asFile().getAbsolutePath());
	}
}
