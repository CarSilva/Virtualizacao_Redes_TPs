FROM ubuntu:latest

RUN apt-get update
RUN apt-get install -y mininet \
                    tcpdump	

RUN apt-get install wget bzip2 -y # needed for wireshark download

RUN apt-get install gcc python -y

RUN apt-get install perl pkg-config libglib2.0-dev libpcap-dev gtk2.0 -y

RUN wget https://1.eu.dl.wireshark.org/src/wireshark-2.2.12.tar.bz2

RUN tar -jxf wireshark-2.2.12.tar.bz2

RUN /wireshark-2.2.12/configure 
RUN make
RUN make install

EXPOSE 6653 6633