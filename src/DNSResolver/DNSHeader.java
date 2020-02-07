package DNSResolver;

import java.io.*;
import java.math.BigInteger;
import java.util.Arrays;

public class DNSHeader {
    private byte[] messageID;
    private byte[] secondRow;
    private byte qr;
    private byte op_code;
    private byte aa;
    private byte tc;
    private byte rd;
    private byte ra;
    private byte z;
    private byte ad;
    private byte cd;
    private byte response_code;
    private short question_count;
    private short answer_count;
    private short authority_count;
    private short additional_count;

    /*
    * read the header from an input stream (we'll use a ByteArrayInputStream but we will only use the basic
    * read methods of input stream to read 1 byte, or to fill in a byte array, so we'll be generic).
    * */
    public static DNSHeader decodeHeader(InputStream is) throws IOException {
        DNSHeader dh = new DNSHeader();
        dh.messageID = is.readNBytes(2);
        dh.secondRow = is.readNBytes(2);
        dh.qr = (byte)(dh.secondRow[0] >> 7 & 1);
        dh.op_code = (byte)(dh.secondRow[0] >> 3 & 0xf);
        dh.aa = (byte)(dh.secondRow[0] >> 2 & 1);
        dh.tc = (byte)(dh.secondRow[0] >> 1 & 1);
        dh.rd = (byte)(dh.secondRow[0] & 1);
        dh.ra = (byte)(dh.secondRow[1] >> 7 & 1);
        dh.z = (byte)(dh.secondRow[1] >> 6 & 1);
        dh.ad = (byte)(dh.secondRow[1] >> 5 & 1);
        dh.cd = (byte)(dh.secondRow[1] >> 4 & 1);
        dh.response_code = (byte)(dh.secondRow[1] & 0xf);
        dh.question_count = new BigInteger(is.readNBytes(2)).shortValue();
        dh.answer_count = new BigInteger(is.readNBytes(2)).shortValue();
        dh.authority_count = new BigInteger(is.readNBytes(2)).shortValue();
        dh.additional_count = new BigInteger(is.readNBytes(2)).shortValue();

        return dh;
    }

    /*
    * This will create the header for the response. It will copy some fields from the request
    * */
    public static DNSHeader buildResponseHeader(DNSMessage request, DNSMessage response) {
        DNSHeader dh = request.getHeader();
        dh.messageID = request.getHeader().messageID;
        dh.qr = 1;
        dh.op_code = request.getHeader().op_code;
        dh.aa = request.getHeader().aa;
        dh.tc = request.getHeader().tc;
        dh.rd = request.getHeader().rd;
        dh.ra = 1;
        dh.z = request.getHeader().z;
        dh.ad = 1;
        dh.cd = request.getHeader().cd;
        dh.response_code = request.getHeader().response_code;
        dh.question_count = request.getHeader().question_count;
        dh.answer_count = 1;
        dh.authority_count = request.getHeader().authority_count;
        dh.additional_count = request.getHeader().additional_count;

        return dh;
    }

    /*
    * encode the header to bytes to be sent back to the client. The OutputStream interface has methods to
    * write a single byte or an array of bytes.
    * */
    public void writeBytes(ByteArrayOutputStream os) throws IOException {
        byte newQRByte = (byte)((qr<<7)|(op_code<<3)|(aa<<2)|(tc<<1)|(rd));
        byte newRAByte = (byte)((ra<<7)|(z<<6)|(ad<<5)|(cd<<4)|(response_code));

        os.write(messageID);
        os.write(newQRByte);
        os.write(newRAByte);
        os.write(DNSMessage.shortToByteArr(question_count));
        os.write(DNSMessage.shortToByteArr(answer_count));
        os.write(DNSMessage.shortToByteArr(authority_count));
        os.write(DNSMessage.shortToByteArr(additional_count));
    }

    @Override
    public String toString() {
        return "DNSHeader{" +
                "messageID=" + Arrays.toString(messageID) +
                ", qr=" + qr +
                ", op_code=" + op_code +
                ", aa=" + aa +
                ", tc=" + tc +
                ", rd=" + rd +
                ", ra=" + ra +
                ", z=" + z +
                ", ad=" + ad +
                ", cd=" + cd +
                ", response_code=" + response_code +
                ", question_count=" + question_count +
                ", answer_count=" + answer_count +
                ", authority_count=" + authority_count +
                ", additional_count=" + additional_count +
                '}';
    }

    public short getAnswer_count() {
        return answer_count;
    }

    public byte getResponse_code() {
        return response_code;
    }
}
