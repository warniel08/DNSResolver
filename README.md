# DNSResolver
Project for my MSD program. Once the localhost server is started it will accept dig as the client to send a DNS request. 
If the DNS header information is not cached locally, the server will forward the request to Google's DNS server. The
reponse will be forwarded to the dig client.

Example console command:
`dig example.com @127.0.0.1 -p 8053`

Example output:
```
; <<>> DiG 9.10.6 <<>> example.com @127.0.0.1 -p 8053
;; global options: +cmd
;; Got answer:
;; ->>HEADER<<- opcode: QUERY, status: NOERROR, id: 58106
;; flags: qr rd ra ad; QUERY: 1, ANSWER: 1, AUTHORITY: 0, ADDITIONAL: 1
;; WARNING: Message has 200 extra bytes at end

;; OPT PSEUDOSECTION:
; EDNS: version: 0, flags:; udp: 512
;; QUESTION SECTION:
;example.com.			IN	A

;; ANSWER SECTION:
example.com.		2424	IN	A	93.184.216.34

;; Query time: 157 msec
;; SERVER: 127.0.0.1#8053(127.0.0.1)
;; WHEN: Thu Feb 06 22:01:06 MST 2020
;; MSG SIZE  rcvd: 256
```
