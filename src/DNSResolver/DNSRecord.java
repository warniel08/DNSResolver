package DNSResolver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

public class DNSRecord {
    private byte leadByte;
    private byte offsetByte;
    private byte[] rName;
    private short rType;
    private short rClass;
    private int ttl;
    private short rDLength;
    private byte[] rData;

    private Calendar expirationDate;

    public static DNSRecord decodeRecord(InputStream is, DNSMessage message) throws Exception {
        DNSRecord dr = new DNSRecord();
        dr.expirationDate = Calendar.getInstance();

        dr.leadByte = (byte)(is.read());
        dr.offsetByte = (byte)(is.read());
        is.mark(2);

        if (dr.leadByte < 0) {
            if (message.getQuestionList().size() > 0) {
                dr.rName = message.getQuestionList().get(0).QName;
                dr.rType = new BigInteger(is.readNBytes(2)).shortValue();
                dr.rClass = new BigInteger(is.readNBytes(2)).shortValue();
            }
        }
        else if (dr.leadByte == 0 && (byte)(is.read()) == 41) {
            is.reset();
            dr.leadByte = 0;
            byte b = (byte)(is.read());
            dr.offsetByte = 0;
            dr.rName = new byte[1];
            Arrays.fill(dr.rName, (byte)(0));
            dr.rType = new BigInteger(is.readNBytes(2)).shortValue();
            dr.rClass = new BigInteger(is.readNBytes(2)).shortValue();
        }
        else if (dr.leadByte == 0) {;
            dr.rName = message.readDomainName(is).toByteArray();
            dr.rType = new BigInteger(is.readNBytes(2)).shortValue();
            dr.rClass = new BigInteger(is.readNBytes(2)).shortValue();
        } else
            throw new Exception();

        dr.ttl = new BigInteger(is.readNBytes(4)).intValue();
        dr.rDLength = new BigInteger(is.readNBytes(2)).shortValue();
        if (dr.rDLength <= 0)
            dr.rData = is.readNBytes(0);
        else
            dr.rData = is.readNBytes(dr.rDLength);

        dr.expirationDate.add(Calendar.SECOND, dr.ttl);

        return dr;
    }

    void writeBytes(ByteArrayOutputStream os, HashMap<String, Integer> hm) throws IOException {
        String qName = new String(rName);
        byte pointer = (byte)(0b11000000);
        ByteBuffer ttlBA = ByteBuffer.allocate(4);
        ttlBA.putInt(ttl);

        if (hm.containsKey(qName)) {
            int offset = hm.get(qName);
            os.write(pointer);
            os.write(offset);
            os.write(DNSMessage.shortToByteArr(rType));
            os.write(DNSMessage.shortToByteArr(rClass));
            os.write(ttlBA.array());
            os.write(DNSMessage.shortToByteArr(rDLength));
            os.write(rData);
        } else {
            os.write(leadByte);
            os.write(rName);
            os.write(41);
            os.write(DNSMessage.shortToByteArr(rType));
            os.write(DNSMessage.shortToByteArr(rClass));
            os.write(ttlBA.array());
            os.write(DNSMessage.shortToByteArr(rDLength));
            os.write(rData);
        }
    }

    @Override
    public String toString() {
        return "DNSRecord{" +
                "leadByte=" + leadByte +
                ", offsetByte=" + offsetByte +
                ", rName=" + Arrays.toString(rName) +
                ", rType=" + rType +
                ", rClass=" + rClass +
                ", ttl=" + ttl +
                ", rDLength=" + rDLength +
                ", rData=" + Arrays.toString(rData) +
                '}';
    }

    /*
    * return whether the creation date + the time to live is after the current time.
    * The Date and Calendar classes will be useful for this.
    * */
    boolean timestampValid() {
        Calendar comparisonTime = Calendar.getInstance();

        return expirationDate.after(comparisonTime);
    }
}
