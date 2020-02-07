package DNSResolver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class DNSMessage {
    // the DNS Header
    private DNSHeader dnsh;
    // an array of questions
    private ArrayList<DNSQuestion> questionList = new ArrayList<>();
    // an array of answers
    private ArrayList<DNSRecord> answerList = new ArrayList<>();
    // an array of "authority records" which we'll ignore
//    private ArrayList<DNSRecord> authRecordsList = new ArrayList<>();
    // an array of "additional records" which we'll almost ignore
    private ArrayList<DNSRecord> additionalRecList = new ArrayList<>();

    static DNSMessage decodeMessage(byte[] bytes) throws Exception {
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        DNSMessage dm = new DNSMessage();

        dm.dnsh = DNSHeader.decodeHeader(is);

        dm.questionList.add(DNSQuestion.decodeQuestion(is, dm));

        if (dm.dnsh.getAnswer_count() > 0) {
            for (int i = 0; i < dm.dnsh.getAnswer_count(); i ++) {
                dm.answerList.add(DNSRecord.decodeRecord(is, dm));
            }
        }
        dm.additionalRecList.add(DNSRecord.decodeRecord(is, dm));

        System.out.println(dm.dnsh.toString());
        System.out.println(dm.questionList.toString());
        System.out.println(dm.answerList.toString());
        System.out.println(dm.additionalRecList.toString());

        return dm;
    }

    DNSHeader getHeader() {
        return dnsh;
    }

    ArrayList<DNSQuestion> getQuestionList() {
        return questionList;
    }

    ArrayList<DNSRecord> getRecordList() {
        return answerList;
    }

    ArrayList<DNSRecord> getAdditionalRecordList() {
        return additionalRecList;
    }

    /*
    * read the pieces of a domain name starting from the current position of the input stream
    * */
    ByteArrayOutputStream readDomainName(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte length = (byte)is.read();
        baos.write(length);

        while (length != 0) {
            baos.write(is.readNBytes(length));
            length = (byte)is.read();
            baos.write(length);
        }

        baos.flush();
        baos.close();

        return baos;
    }

    /*
    * same, but used when there's compression and we need to find the domain from earlier in the message.
    * This method should make a ByteArrayInputStream that starts at the specified byte and call the other
    * version of this method
    * */
//    ByteArrayOutputStream readDomainName(int firstByte) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ByteArrayInputStream is =
//        readDomainName();
//        return baos;
//    }

    /*
    * build a response based on the request and the answers you intend to send back.
    * */
    static DNSMessage buildResponse(DNSMessage request, ArrayList<DNSRecord> answers) {
        DNSMessage response = new DNSMessage();
        response.questionList = request.getQuestionList();
        response.answerList = answers;
        response.additionalRecList = request.getAdditionalRecordList();
        response.dnsh = DNSHeader.buildResponseHeader(request, response);
        return response;
    }

    /*
    * get the bytes to put in a packet and send back
    * */
    byte[] toBytes() throws IOException {
        HashMap<String, Integer> hm = new HashMap<>();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dnsh.writeBytes(baos);
        questionList.get(0).writeBytes(baos, hm);
        if (answerList.size() > 0)
            answerList.get(0).writeBytes(baos, hm);
        if (additionalRecList.size() > 0)
            additionalRecList.get(0).writeBytes(baos,hm);

        return baos.toByteArray();
    }

    static byte[] shortToByteArr(short s) {
        ByteBuffer buf = ByteBuffer.allocate(2);
        buf.putShort(s);
        return buf.array();
    }

    /*
    * If this is the first time we've seen this domain name in the packet, write it using the DNS encoding
    * (each segment of the domain prefixed with its length, 0 at the end), and add it to the hash map.
    * Otherwise, write a back pointer to where the domain has been seen previously.
    * */
//    static void writeDomainName(ByteArrayOutputStream, HashMap<String,Integer> domainLocations, String[] domainPieces);

    /*
    * join the pieces of a domain name with dots ([ "utah", "edu"] -> "utah.edu" )
    * */
//    String octetsToString(String[] octets);


    @Override
    public String toString() {
        return "DNSMessage{" +
                "dnsh=" + dnsh +
                ", questionList=" + questionList +
                ", answerList=" + answerList +
                ", additionalRecList=" + additionalRecList +
                '}';
    }
}
