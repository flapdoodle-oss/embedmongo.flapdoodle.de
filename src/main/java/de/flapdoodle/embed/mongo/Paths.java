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
import de.flapdoodle.embed.mongo.util.LinuxDistribution;
import de.flapdoodle.embed.process.config.store.FileSet;
import de.flapdoodle.embed.process.config.store.FileType;
import de.flapdoodle.embed.process.config.store.IPackageResolver;
import de.flapdoodle.embed.process.distribution.ArchiveType;
import de.flapdoodle.embed.process.distribution.BitSize;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.distribution.IVersion;
import de.flapdoodle.embed.process.distribution.Platform;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 *
 */
public class Paths implements IPackageResolver {

    protected static final String WINDOWS_2012_PLUS_STRING = "2012plus-";
    protected static final String WINDOWS_2008_PLUS_STRING = "2008plus-";
    protected static final String WINDOWS_ANY_VERSION_STRING = "";
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
                versionStr = getWindowsMinVersion(distribution)
                        + (withSsl(distribution) ? "ssl-": "")
                        + versionStr;
        } else if (distribution.getPlatform()==Platform.Linux) {
                versionStr = getLinuxPlatform(distribution)
                        + versionStr;
        }

		if (distribution.getPlatform() == Platform.OS_X && withSsl(distribution) ) {
            return platformStr + "/mongodb-" + platformStr + "-ssl-" + bitSizeStr + "-" + versionStr + "." + archiveTypeStr;
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

    protected String getWindowsMinVersion(Distribution distribution) {
        String osName = System.getProperty("os.name");
        if (osName.contains("Windows Server 2012 R2")
                || (distribution.getVersion() instanceof IFeatureAwareVersion)
                && ((IFeatureAwareVersion) distribution.getVersion()).enabled(Feature.ONLY_WINDOWS_2012_SERVER)) {
            return WINDOWS_2012_PLUS_STRING;
        } else if (osName.contains("Windows Server 2008 R2")
                || (distribution.getVersion() instanceof IFeatureAwareVersion)
                && ((IFeatureAwareVersion) distribution.getVersion()).enabled(Feature.ONLY_WINDOWS_2008_SERVER)) {
            return WINDOWS_2008_PLUS_STRING;
        } else if (osName.contains("Windows 7")) {
            return WINDOWS_2008_PLUS_STRING;
        } else {
            return WINDOWS_ANY_VERSION_STRING;
        }
    }

    private String closestLinuxPlatform(List<String> platforms, String version) {
        if (platforms.isEmpty()) {
            return "";
        }

        for (String platform : platforms) {
            List<LinuxDistribution> candidates = LinuxDistribution.match(platform);
            if (!candidates.isEmpty()) {
                String[] versionComponents = version.split("\\.");
                int versionMajor = Integer.parseInt(versionComponents[0], 10);
                Optional<LinuxDistribution> matchingDistro = candidates.stream()
                        .filter(c -> c.getMajor() <= versionMajor)
                        .max(Comparator.comparingInt(LinuxDistribution::getMajor).thenComparingInt(LinuxDistribution::getMinor));

                if (matchingDistro.isPresent()) {
                    return matchingDistro.get().getPlatform() + "-";
                }
            }
        }

        return "";
    }

    protected String getLinuxPlatform(Distribution distribution) {
        if ((distribution.getVersion() instanceof IFeatureAwareVersion)
                && ((IFeatureAwareVersion) distribution.getVersion()).enabled(Feature.NO_GENERIC_LINUX)) {
            File osRelease = new File("/etc/os-release");
            if (osRelease.exists()) {
                // Attempt to derive the specific linux platform from the OS release information
                try {
                    List<String> lines = Files.readAllLines(osRelease.toPath());
                    List<String> possiblePlatforms = new ArrayList<>();
                    String version = null;
                    for (String line : lines) {
                        if (line.startsWith("ID=")) {
                            String value = line.substring(3).replaceAll("\"", "").trim();
                            possiblePlatforms.add(0, value);
                        } else if (line.startsWith("ID_LIKE=")) {
                            String value = line.substring(8).replaceAll("\"", "").trim();
                            possiblePlatforms.addAll(Arrays.asList(value.split(" ")));
                        } else if (line.startsWith("VERSION_ID=")) {
                            version = line.substring(11).replaceAll("\"", "").trim();
                        }
                    }

                    return closestLinuxPlatform(possiblePlatforms, version);
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        return "";
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
