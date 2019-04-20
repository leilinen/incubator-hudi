package com.uber.hoodie.utilities.sources.decoder;

import com.uber.hoodie.exception.HoodieException;
import kafka.serializer.Decoder;
import kafka.utils.VerifiableProperties;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class HoodieKafkaAvroDecoder implements Decoder<Object> {
    private static Logger logger = LogManager.getLogger(HoodieKafkaAvroDecoder.class);
    private Schema schema;
    private File schemaFile;
    private VerifiableProperties props;

    /**
     * Configs supported
     */
    private static final String SOURCE_SCHEMA_FILE_PROP = "hoodie.deltastreamer.schemaprovider.source.schema.file";
    private static final String TARGET_SCHEMA_FILE_PROP = "hoodie.deltastreamer.schemaprovider.target.schema.file";

    public HoodieKafkaAvroDecoder(VerifiableProperties properties) {
        this.props = properties;
        targetSchemaProvider();
    }

    @Override
    public Object fromBytes(byte[] bytes) {
        return this.deserialize(bytes);
    }

    private Object deserialize(byte[] bytes) {
        GenericRecord record = null;

        try{
           DatumReader<GenericRecord> recordDatumReader = new SpecificDatumReader<>(schema);
           BinaryDecoder binaryEncoder = DecoderFactory.get().directBinaryDecoder(new ByteArrayInputStream(bytes), null);
           record = recordDatumReader.read(record, binaryEncoder);
        }
        catch(IOException e){
           logger.error(e.getMessage());
        }
        if (!record.get("cur").equals("{}")){
            record.put("cur", "{}");
        }
        return record;
    }

    private void targetSchemaProvider() {
        FileSystem fs = new HoodieHdfsProperties(this.props.props()).getFileSystem();
        if (fs == null) {
            throw new HoodieException("Cannot connect to file system to parse avro schema!");
        }
        try {
            this.schema = new Schema.Parser().parse(fs.open(new Path(props.getString(this.TARGET_SCHEMA_FILE_PROP))));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }


}

