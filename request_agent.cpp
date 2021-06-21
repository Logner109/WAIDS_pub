#include <string>
#include <iostream>
#include <fstream>
#include <stdexcept>
#include "tins/tcp_ip/stream_follower.h"
#include "tins/tins.h"
#include "tins/sniffer.h"
#include "curl/curl.h"

using std::string;
using std::cout;
using std::cerr;
using std::endl;
using std::exception;
using std::ifstream;

using Tins::Packet;
using Tins::Sniffer;
using Tins::SnifferConfiguration;
using Tins::TCPIP::Stream;
using Tins::TCPIP::StreamFollower;

unsigned char* ustrcat(unsigned char *dest, const unsigned char *src) {
  unsigned int length_1 = 0;
  unsigned int length_2 = 0;
  // find the end of the first unsigned char string
  for (; dest[length_1] != '\0'; length_1++);
  //input unsigned chars into the string from that position to the end
  for (; src[length_2] != '\0'; length_2++)
    dest[length_1+length_2] = src[length_2];
  // place a NULL terminating character at the end
  dest[length_1+length_2] = '\0';
  return dest;
}

// This example captures and follows TCP streams seen on port 80. It will
// wait until both the client and server send data and then apply a regex
// to both payloads, extrating some information and printing it.

// Don't buffer more than 3kb of data in either request/response
const size_t MAX_PAYLOAD = 3 * 1024;

//request_agent.config parameters 
string CAPTURE;
string INTERFACE;
string URL;

void on_server_data(Stream& stream) {
    //Process servers data
    if(stream.server_payload().size() > MAX_PAYLOAD){
    	stream.ignore_server_data();
    }
}

void on_client_data(Stream& stream) {
    //Get a clients payload, vector<uint8_t>
    const Stream::payload_type& payload = stream.client_payload();
    unsigned char* IP;
    
    if(stream.is_v6() == false){
    	Tins::IPv4Address ip(stream.client_addr_v4());
    	IP = (unsigned char*)ip.to_string().c_str();
    }
    
    
    if(payload.size() > MAX_PAYLOAD){
    	stream.ignore_client_data();
    }
    
    unsigned char B[4096] = "";
    B[0] = 'i';
    B[1] = '=';
    ustrcat(B, IP);
    unsigned char C[2];
    C[0] = '&';
    C[1] = '\0';
    ustrcat(B, C);
    //printf("%s\n", B);
    //unsigned char ip[stream.client_addr_v4().size()] = stream.client_addr_v4();
   
    unsigned char A[MAX_PAYLOAD] = "";
    A[0] = 'r';
    A[1] = '=';
    ustrcat(A,payload.data());
    
    
    ustrcat(B, A);
    printf("%s\n", B);
    //---------------------------------------------
    CURL *curl;
    CURLcode res;
    curl = curl_easy_init();
    if(curl){
      curl_easy_setopt(curl, CURLOPT_URL, URL);
      curl_easy_setopt(curl, CURLOPT_POSTFIELDS, B);
      res = curl_easy_perform(curl);
      curl_easy_cleanup(curl);
    }
    curl_global_cleanup();
    //---------------------------------------------
}

void on_new_connection(Stream& stream) {
    //Client and Server data callbacks
    stream.client_data_callback(&on_client_data);
    stream.ignore_server_data();
    stream.auto_cleanup_payloads(true);
}

void set_variable(string line){
   string delimiter = "=";
   string variable, value;
   size_t pos = 0;
   
   if((pos = line.find(delimiter)) != string::npos){
      variable = line.substr(0, pos);
      value = line.substr(pos+1, line.length());
   }
   
   if(variable == "capture"){
      CAPTURE = value;
   }else if(variable == "interface"){
      INTERFACE = value;
   }else if(variable == "url"){
      URL = value;
   }
   
   cout << variable << " : " << value << endl;
}

int main(int argc, char* argv[]) {
    try {
    	string line;
    	ifstream configuration;
    	configuration.open("request_agent.conf");
    	cout << "Configuration:" << "\n\n";
    	if(configuration.is_open()){
    	   while(getline (configuration,line)){
    	      if(line[0] != '/'){
    	         set_variable(line);
    	      }
    	   }
    	   configuration.close();
    	}else{
    	   cout << "Unable to open configuration file" << endl;
    	   return 0;
    	}
    	
        // Construct the sniffer configuration object
        SnifferConfiguration config;
        // Only capture TCP traffic sent from/to port 80
        config.set_filter(CAPTURE);
        // Construct the sniffer we'll use
        Sniffer sniffer(INTERFACE, config);

        cout << endl << "Starting capture on interface " << INTERFACE << endl;

        // Now construct the stream follower
        StreamFollower follower;
        // We just need to specify the callback to be executed when a new 
        // stream is captured. In this stream, you should define which callbacks
        // will be executed whenever new data is sent on that stream 
        // (see on_new_connection)
        follower.new_stream_callback(&on_new_connection);
        // Now start capturing. Every time there's a new packet, call 
        // follower.process_packet
        sniffer.sniff_loop([&](Packet& packet) {
            follower.process_packet(packet);
            return true;
        });
    }
    catch (exception& ex) {
        cerr << "Error: " << ex.what() << endl;
        return 1;
    }
}
