package DNSResolver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class DNSQuestion {
    protected byte[] QName;
    private short QType;
    private short QClass;

    /*
    * read a question from the input stream. Due to compression, you may have to ask the DNSMessage
    * containing this question to read some of the fields.
    * */
    static DNSQuestion decodeQuestion(InputStream is, DNSMessage message) throws Exception {
        DNSQuestion dq = new DNSQuestion();
        ByteArrayOutputStream baos = message.readDomainName(is);

        dq.QName = baos.toByteArray();
        dq.QType = new BigInteger(is.readNBytes(2)).shortValue();
        dq.QClass = new BigInteger(is.readNBytes(2)).shortValue();

        return dq;
    }

     /*
    * Write the question bytes which will be sent to the client. The hash map is used for us to compress the message,
    * see the DNSMessage class below.
    * */
    void writeBytes(ByteArrayOutputStream os, HashMap<String,Integer> hm) throws IOException {
        int location = os.size();

        for (int i = 0; i < QName.length; i++) {
            os.write(QName[i]);
        }
        os.write(DNSMessage.shortToByteArr(QType));
        os.write(DNSMessage.shortToByteArr(QClass));

        String hashStr = new String(QName);
        hm.put(hashStr, location);
    }

    /*
     * Let your IDE generate these. They're needed to use a question as a HashMap key, and to get a human readable string.
     * */
    @Override
    public String toString() {
        return "DNSQuestion{" +
                " QName=" + Arrays.toString(QName) +
                ", QType=" + QType +
                ", QClass=" + QClass +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DNSQuestion that = (DNSQuestion) o;
        return QType == that.QType &&
                QClass == that.QClass &&
                Arrays.equals(QName, that.QName);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(QType, QClass);
        result = 31 * result + Arrays.hashCode(QName);
        return result;
    }
}
