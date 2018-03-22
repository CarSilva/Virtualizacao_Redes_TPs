FROM ubuntu:latest	
RUN apt-get update
RUN apt-get install -y git
RUN apt-get install -y tcpdump	
RUN DEBIAN_FRONTEND=noninteractive apt-get install -y wireshark
WORKDIR /home/ubuntu
RUN apt-get install -y sudo
RUN apt-get install -y net-tools
RUN apt-get install -y iputils-ping
RUN git clone https://github.com/mininet/mininet
RUN mininet/util/install.sh -a
EXPOSE 6653 6633

COPY ENTRYPOINT.sh /home/ubuntu/

ENTRYPOINT ["/home/ubuntu/ENTRYPOINT.sh"]

