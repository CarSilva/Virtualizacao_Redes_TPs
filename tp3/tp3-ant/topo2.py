from mininet.node import CPULimitedHost
from mininet.topo import Topo
from mininet.net import Mininet
from mininet.log import setLogLevel, info
from mininet.node import RemoteController, OVSSwitch
from mininet.cli import CLI
from mininet.link import Link, TCLink

class Anycast(Topo):

    def __init__(self, **opts):
        # Initialize topology
        # It uses the constructor for the Topo cloass
        super(Anycast, self).__init__(**opts)

        # Add hosts and switches
        h1 = self.addHost('h1')
        h2 = self.addHost('h2', mac='00:00:00:00:00:09')
        h3 = self.addHost('h3', mac='00:00:00:00:00:10')

        # Adding switches
        s1 = self.addSwitch('s1', protocols='OpenFlow13')
        s2 = self.addSwitch('s2', protocols='OpenFlow13')

        # Add links
        self.addLink(s1, s2)
        self.addLink(h1, s1)
        self.addLink(s2, h2)
        self.addLink(s2, h3)


def run():
    c = RemoteController('c', '127.0.0.1', 6653)
    net = Mininet(topo=Anycast(), host=CPULimitedHost, controller=None, switch=OVSSwitch)
    net.addController(c)

    h2 = net.get('h2')
    h3 = net.get('h3')
    s2 = net.get('s2')
    #Link(h2, s2, intfName1='h2-eth1')
    #Link(h3, s2, intfName1='h3-eth1')
    h2.cmd('ifconfig h2-eth0:1 10.0.0.250 netmask 255.0.0.0')
    h3.cmd('ifconfig h3-eth0:1 10.0.0.250 netmask 255.0.0.0')
    net.start()
    CLI(net)
    net.stop()

# if the script is run directly (sudo custom/optical.py):
if __name__ == '__main__':
    setLogLevel('info')
    run()
