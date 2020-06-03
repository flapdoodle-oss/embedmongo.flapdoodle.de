/**
 * Copyright (C) 2020
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
package de.flapdoodle.embed.mongo.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum LinuxDistribution {
    AMAZON("Amazon", 1, 0, "amazon"),
    AMAZON_2("Amazon", 2, 0, "amazon2"),
    DEBIAN_9("Debian", 9, 0, "debian92"),
    DEBIAN_10("Debian", 10, 0, "debian10"),
    RHEL_6_2("RHEL", 6, 2, "rhel62"),
    RHEL_7_0("RHEL", 7, 0, "rhel70"),
    RHEL_8_0("RHEL", 8, 0, "rhel80"),
    SUSE_12("SUSE", 12, 0, "suse12"),
    SUSE_15("SUSE", 15, 0, "suse15"),
    UBUNTU_16_04("Ubuntu", 16, 4, "ubuntu1604"),
    UBUNTU_18_04("Ubuntu", 18, 4, "ubuntu1804");

    private final String distro;
    private final int major;
    private final int minor;
    private final String platform;

    private LinuxDistribution(String distribution, int majorVersion, int minorVersion, String platform) {
        this.distro = distribution.toLowerCase();
        this.major = majorVersion;
        this.minor = minorVersion;
        this.platform = platform;
    }

    public String getDistro() {
        return distro;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public String getPlatform() {
        return platform;
    }

    public static List<LinuxDistribution> match(String distribution) {
        return Arrays.stream(values()).filter(d -> d.distro.equalsIgnoreCase(distribution)).collect(Collectors.toList());
    }
}
