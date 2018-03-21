FROM ubuntu:latest
VOLUME /home/ubuntu
WORkDIR /home/ubuntu
RUN apt-get update
RUN apt-get install -y mininet
RUN apt-get install -y tcpdump	
RUN DEBIAN_FRONTEND=noninteractive apt-get install wireshark -y

EXPOSE 6653 6633