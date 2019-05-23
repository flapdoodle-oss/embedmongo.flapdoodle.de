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
package de.flapdoodle.embed.mongo;

import de.flapdoodle.embed.mongo.distribution.Feature;
import de.flapdoodle.embed.mongo.distribution.IFeatureAwareVersion;
import de.flapdoodle.embed.process.config.store.FileSet;
import de.flapdoodle.embed.process.config.store.FileType;
import de.flapdoodle.embed.process.config.store.IPackageResolver;
import de.flapdoodle.embed.process.distribution.ArchiveType;
import de.flapdoodle.embed.process.distribution.BitSize;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.distribution.IVersion;
import de.flapdoodle.embed.process.distribution.Platform;
import de.flapdoodle.embed.process.distribution.Architecture;

/**
 *
 */
public class Paths implements IPackageResolver {

	private final Command command;

	public Paths(Command command) {
		this.command=command;
	}

	@Override
	public FileSet getFileSet(Distribution distribution) {
		String executableFileName;
		switch (distribution.getPlatform()) {
			case Linux:
			case OS_X:
			case Solaris:
			case FreeBSD:
				executableFileName = command.commandName();
				break;
			case Windows:
				executableFileName = command.commandName()+".exe";
				break;
			default:
				throw new IllegalArgumentException("Unknown Platform " + distribution.getPlatform());
		}
		return FileSet.builder().addEntry(FileType.Executable, executableFileName).build();
	}

	//CHECKSTYLE:OFF
	@Override
	public ArchiveType getArchiveType(Distribution distribution) {
		ArchiveType archiveType;
		switch (distribution.getPlatform()) {
			case Linux:
			case OS_X:
			case Solaris:
			case FreeBSD:
				archiveType = ArchiveType.TGZ;
				break;
			case Windows:
				archiveType = ArchiveType.ZIP;
				break;
			default:
				throw new IllegalArgumentException("Unknown Platform " + distribution.getPlatform());
		}
		return archiveType;
	}

	@Override
	public String getPath(Distribution distribution) {
		String versionStr = getVersionPart(distribution.getVersion());

		if (distribution.getPlatform() == Platform.Solaris && isFeatureEnabled(distribution, Feature.NO_SOLARIS_SUPPORT)) {
		    throw new IllegalArgumentException("Mongodb for solaris is not available anymore");
        }

		ArchiveType archiveType = getArchiveType(distribution);
		String archiveTypeStr = getArchiveString(archiveType);

        String platformStr = getPlattformString(distribution);

        String bitSizeStr = getBitSize(distribution);

        if ((distribution.getBitsize()==BitSize.B64) && (distribution.getPlatform()==Platform.Windows)) {
				versionStr = (useWindows2008PlusVersion(distribution) ? "2008plus-": "")
                        + (withSsl(distribution) ? "ssl-": "")
                        + versionStr;
		}
		if (distribution.getPlatform() == Platform.OS_X && withSsl(distribution) ) {
            return platformStr + "/mongodb-" + platformStr + "-ssl-" + bitSizeStr + "-" + versionStr + "." + archiveTypeStr;
        }
		// all arm64 distributions has ubuntu prefix
		if (distribution.getArchitecture() == Architecture.AARCH64 && isFeatureEnabled(distribution, Feature.AARCH64_AVAIL)) {
            return platformStr + "/mongodb-" + platformStr + "-arm64-ubuntu1604-" + versionStr + "." + archiveTypeStr;
        }
		return platformStr + "/mongodb-" + platformStr + "-" + bitSizeStr + "-" + versionStr + "." + archiveTypeStr;
	}

    private String getArchiveString(ArchiveType archiveType) {
        String sarchiveType;
        switch (archiveType) {
            case TGZ:
                sarchiveType = "tgz";
                break;
            case ZIP:
                sarchiveType = "zip";
                break;
            default:
                throw new IllegalArgumentException("Unknown ArchiveType " + archiveType);
        }
        return sarchiveType;
    }

    private String getPlattformString(Distribution distribution) {
        String splatform;
        switch (distribution.getPlatform()) {
            case Linux:
                splatform = "linux";
                break;
            case Windows:
                splatform = "win32";
                break;
            case OS_X:
                splatform = "osx";
                break;
            case Solaris:
                splatform = "sunos5";
                break;
            case FreeBSD:
                splatform = "freebsd";
                break;
            default:
                throw new IllegalArgumentException("Unknown Platform " + distribution.getPlatform());
        }
        return splatform;
    }

    private String getBitSize(Distribution distribution) {
        String sbitSize;
        switch (distribution.getBitsize()) {
            case B32:
                if (distribution.getVersion() instanceof IFeatureAwareVersion) {
                    IFeatureAwareVersion featuredVersion = (IFeatureAwareVersion) distribution.getVersion();
                    if (featuredVersion.enabled(Feature.ONLY_64BIT)) {
                        throw new IllegalArgumentException("this version does not support 32Bit: "+distribution);
                    }
                }

                switch (distribution.getPlatform()) {
                    case Linux:
                        sbitSize = "i686";
                        break;
                    case Windows:
                        sbitSize = "i386";
                        break;
                    case OS_X:
                        sbitSize = "i386";
                        break;
                    default:
                        throw new IllegalArgumentException("Platform " + distribution.getPlatform() + " not supported yet on 32Bit Platform");
                }
                break;
            case B64:
                sbitSize = "x86_64";
                break;
            default:
                throw new IllegalArgumentException("Unknown BitSize " + distribution.getBitsize());
        }
        return sbitSize;
    }

    protected boolean useWindows2008PlusVersion(Distribution distribution) {
	    String osName = System.getProperty("os.name");
        if (osName.contains("Windows Server 2008 R2")
                || (distribution.getVersion() instanceof IFeatureAwareVersion)
                && ((IFeatureAwareVersion) distribution.getVersion()).enabled(Feature.ONLY_WINDOWS_2008_SERVER))  {
            return true;
        } else {
            return osName.contains("Windows 7");
        }
	}

	protected boolean withSsl(Distribution distribution) {
        if ((distribution.getPlatform() == Platform.Windows || distribution.getPlatform() == Platform.OS_X)
                && distribution.getVersion() instanceof IFeatureAwareVersion) {
            return ((IFeatureAwareVersion) distribution.getVersion()).enabled(Feature.ONLY_WITH_SSL);
        } else {
            return false;
        }
    }

    private static boolean isFeatureEnabled(Distribution distribution, Feature feature) {
	    return (distribution.getVersion() instanceof IFeatureAwareVersion
                &&  ((IFeatureAwareVersion) distribution.getVersion()).enabled(feature));
    }

	protected static String getVersionPart(IVersion version) {
		return version.asInDownloadPath();
	}

}
