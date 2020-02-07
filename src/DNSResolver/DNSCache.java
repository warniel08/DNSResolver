package DNSResolver;

import java.util.HashMap;

public class DNSCache {
    private static HashMap<DNSQuestion, DNSRecord> hm = new HashMap<>();

    DNSRecord query(DNSQuestion dq) {
        if (hm.containsKey(dq) && hm.get(dq).timestampValid()) {
            return hm.get(dq);
        } else {
            hm.remove(dq);
            return null;
        }
    }

    void insert(DNSQuestion dq, DNSRecord dr) {
        hm.put(dq, dr);
    }
}
