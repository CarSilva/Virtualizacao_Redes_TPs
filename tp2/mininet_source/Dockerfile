FROM ubuntu:latest	
RUN apt-get update
RUN apt-get install -y git
RUN apt-get install -y tcpdump	
RUN DEBIAN_FRONTEND=noninteractive apt-get install -y wireshark
WORKDIR /home/ubuntu
COPY ENTRYPOINT.sh /
RUN apt-get install -y sudo
RUN apt-get install -y net-tools
RUN apt-get install -y iputils-ping
RUN git clone https://github.com/mininet/mininet
RUN mininet/util/install.sh -a
RUN chmod +x /ENTRYPOINT.sh
EXPOSE 6653 6633
ENTRYPOINT ["/ENTRYPOINT.sh"]

