from mininet.topo import Topo
class Topology( Topo ):
    def __init__( self ):
        Topo.__init__( self )
        s1 = self.addSwitch( 's1' )
        s2 = self.addSwitch( 's2' )
        h1 = self.addHost( 'h1' )
        h2 = self.addHost( 'h2', ip='10.0.0.2', mac='00:00:00:00:00:09')
        h3 = self.addHost( 'h3', ip='10.0.0.3', mac='00:00:00:00:00:10')
        self.addLink( s1, s2 )
        self.addLink( s1, h1 )
        self.addLink( s2, h2 )
        self.addLink( s2, h3 )
        self.addLink( s2, h3 )
        self.addLink( s2, h2 )

topos = { 'mytopo': ( lambda: Topology() ) }

