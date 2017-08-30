package de.flapdoodle.embed.mongo;

import de.flapdoodle.embed.mongo.config.*;
import de.flapdoodle.embed.mongo.distribution.IFeatureAwareVersion;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.config.store.IDownloadConfig;
import de.flapdoodle.embed.process.config.store.ITimeoutConfig;
import de.flapdoodle.embed.process.config.store.TimeoutConfigBuilder;
import de.flapdoodle.embed.process.io.IStreamProcessor;
import de.flapdoodle.embed.process.io.Processors;
import de.flapdoodle.embed.process.io.directories.FixedPath;
import de.flapdoodle.embed.process.runtime.ICommandLinePostProcessor;
import de.flapdoodle.embed.process.store.IArtifactStore;

import java.io.IOException;

import static de.flapdoodle.embed.mongo.Command.MongoD;

public class MongoDownloadAndExtract {

    public static void main(String[] args) throws IOException {

        MongoDownloadAndExtract mongoDownloadAndExtract = new MongoDownloadAndExtract();
        String cacheDir = args[0];
        String dbVersion = args[1];
        mongoDownloadAndExtract.downloadArtifactAndExtract(cacheDir, dbVersion);
    }

    public void downloadArtifactAndExtract(String cacheDir,
                                           String dbVersion) throws IOException {

        IDownloadConfig downloadConfig = buildDownloadConfig(cacheDir);
        IMongodConfig mongoConfig = buildMongodConfig(resolveVersion(dbVersion));

        OutputBufferProcessor outputBuffer = new OutputBufferProcessor();

        IRuntimeConfig runtimeConfig = buildRuntimeConfig(outputBuffer, downloadConfig);
        MongodStarter runtime = MongodStarter.getInstance(runtimeConfig);

        runtime.prepare(mongoConfig);

        System.out.println("Done");
    }

    private Version resolveVersion(final String dbVersion) {
        String versionStr = "V" + dbVersion.replace('.', '_');
        return Version.valueOf(versionStr);
    }

    private IRuntimeConfig buildRuntimeConfig(IStreamProcessor outputBuffer,
                                              IDownloadConfig downloadConfig) {

        return new RuntimeConfigBuilder()
                .processOutput(new ProcessOutput(outputBuffer, outputBuffer, Processors.silent()))
                .commandLinePostProcessor(new ICommandLinePostProcessor.Noop())
                .artifactStore(buildArtifactStore(downloadConfig))
                .build();
    }

    private IArtifactStore buildArtifactStore(IDownloadConfig downloadConfig) {
        return new ExtractedArtifactStoreBuilder()
                .defaults(MongoD)
                .download(downloadConfig)
                .extractDir(downloadConfig.getArtifactStorePath())
                .build();
    }


    private IDownloadConfig buildDownloadConfig(String cacheDir) {
        System.out.println("cacheDir: " + cacheDir);
        return new DownloadConfigBuilder()
                .defaultsForCommand(MongoD)
                .timeoutConfig(buildTimeoutConfig())
                .artifactStorePath(new FixedPath(cacheDir))
                .build();
    }

    private ITimeoutConfig buildTimeoutConfig() {
        return new TimeoutConfigBuilder()
                .connectionTimeout(10000)
                .readTimeout(60000)
                .build();
    }

    private IMongodConfig buildMongodConfig(IFeatureAwareVersion version) throws IOException {
        return new MongodConfigBuilder()
                .version(version)
                .build();
    }

    class OutputBufferProcessor implements IStreamProcessor {

        int capacity;

        public OutputBufferProcessor(int capacity) {
            this.capacity = capacity;
        }

        public OutputBufferProcessor(){
            this(4096);
        }

        private StringBuilder blocks = new java.lang.StringBuilder(capacity);

        public void process(String block) {
            blocks.append(block);
        }

        public void onProcessed() {
            blocks.trimToSize();
        }

        public String toString() {
            return blocks.toString();
        }

    }
}
