from mininet.node import CPULimitedHost
from mininet.topo import Topo
from mininet.net import Mininet
from mininet.log import setLogLevel, info
from mininet.node import RemoteController, OVSSwitch
from mininet.cli import CLI
from mininet.link import Link, TCLink
import os

class Anycast(Topo):

    def __init__(self, **opts):
        # Initialize topology
        # It uses the constructor for the Topo cloass
        super(Anycast, self).__init__(**opts)

        # Add hosts and switches
        client1 = self.addHost('client1')
        client2 = self.addHost('client2')
        dns1 = self.addHost('dns1')
        dns2 = self.addHost('dns2')
        fs1 = self.addHost('fs1')
        fs2 = self.addHost('fs2')

        # Adding switches
        s1 = self.addSwitch('s1', protocols='OpenFlow13')
        s2 = self.addSwitch('s2', protocols='OpenFlow13')
        s3 = self.addSwitch('s3', protocols='OpenFlow13')
        s4 = self.addSwitch('s4', protocols='OpenFlow13')
        s5 = self.addSwitch('s5', protocols='OpenFlow13')
        s6 = self.addSwitch('s6', protocols='OpenFlow13')
        s7 = self.addSwitch('s7', protocols='OpenFlow13')
        s8 = self.addSwitch('s8', protocols='OpenFlow13')
        s9 = self.addSwitch('s9', protocols='OpenFlow13')

        # Add links
        self.addLink(s1, s2)
        self.addLink(s1, s4)
        self.addLink(s1, fs1)
        self.addLink(s1, fs2)
        self.addLink(s2, s3)
        self.addLink(s2, s5)
        self.addLink(s2, client1)
        self.addLink(s3, s6)
        self.addLink(s3, dns1)
        self.addLink(s4, s5)
        self.addLink(s4, s7)
        self.addLink(s5, s6)
        self.addLink(s5, s8)
        self.addLink(s6, s9)
        self.addLink(s7, dns2)
        self.addLink(s7, s8)
        self.addLink(s8, s9)
        self.addLink(s9, client2)



def run():
    c = RemoteController('c', '127.0.0.1', 6653)
    net = Mininet(topo=Anycast(), host=CPULimitedHost, controller=None, switch=OVSSwitch)
    net.addController(c)

    dns1 = net.get('dns1')
    dns2 = net.get('dns2')
    fs1 = net.get('fs1')
    fs2 = net.get('fs2')
    client1 = net.get('client1')
    client2 = net.get('client2')

    #Link(h2, s2, intfName1='h2-eth1')
    #Link(h3, s2, intfName1='h3-eth1')
    client1.cmd('ifconfig client1-eth0 10.0.0.1 netmask 255.0.0.0')
    client2.cmd('ifconfig client2-eth0 10.0.0.2 netmask 255.0.0.0')
    dns1.cmd('ifconfig dns1-eth0:1 10.0.0.249 netmask 255.0.0.0')
    dns2.cmd('ifconfig dns2-eth0:1 10.0.0.249 netmask 255.0.0.0')
    fs1.cmd('ifconfig fs1-eth0:1 10.0.0.250 netmask 255.0.0.0')
    fs2.cmd('ifconfig fs2-eth0:1 10.0.0.250 netmask 255.0.0.0')
    dns1.cmd("apt-get install --yes bind9")
    dns1.cmd("service bind9 restart")
    net.start()
    CLI(net)
    net.stop()

# if the script is run directly (sudo custom/optical.py):
if __name__ == '__main__':
    setLogLevel('info')
    run()
    os.system("sudo mn -c")
