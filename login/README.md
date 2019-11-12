# Building

## Docker

Build image: `docker build -t bacta-login .`<br>
Create Container: `docker run --name bacta-login -p 44453:44453/udp -p 8080:8080/tcp bacta-login`<br>
Start Container: `docker start bacta-login`<br>
Stop Container: `docker stop bacta-login`

# Introduction
The login server services SWG clients before they have chosen which character to play
or which galaxy to play upon.

## Galaxy Listing
The login server presents a list of galaxies to the player from which they may choose
to create a new character or play an existing character on a known galaxy.

The listing is comprised of galaxies that are *trusted* by the login server. The process
for a galaxy to become trusted by the login server depends on how the login server is
configured.

### Galaxy Registration
The typical process for galaxy registration is manual entry into the galaxy cluster
database. The login server uses this database as the source of truth for determining
if a galaxy is trusted or not.

A galaxy is expected to identify itself with the login server by sending the
`GalaxyServerId` message to a respective login server. This message should be sent in
the following scenarios:

1. When the galaxy server is first starting, it will send the `GalaxyServerId` message
to all configured login servers.
2. When the login server is first starting, it will send the `LoginServerOnline` message
to all trusted galaxy servers in its galaxy cluster database. Galaxies are expected to
follow receipt of this message with the `GalaxyServerId` message. This is helpful in the
situation that the login server was recycled for some reason. This gives galaxy servers
which are still running the chance to identify again with the login server.

### Automatic Galaxy Registration
A configuration option exists to allow the login server to automatically register untrusted
galaxy servers that send it a `GalaxyServerId` message. This is intended to be used in local
development environments only. However, if you can ensure that no external network access is
available for communication to reach your login server, and you can trust any communication
on your network, then you may choose to enable this option. This may be the case if you are
running a local LAN server behind a firewall.

Upon receiving the `GalaxyServerId` message, the login server will add an entry to the galaxy
cluster database for the _address_ and _port_ combination of the incoming request. Then as usual
it will issue a `GalaxyServerIdAck` containing the unique id assigned to the galaxy server.

### Updating Galaxy Status
When a galaxy's status changes, it is expected not notify the login server. Status changes may
include the galaxy's name, connection status information (online, offline, locked, loading), max
characters allowed to be created, max characters allowed per account, maximum number of players
allowed online, etc.

The galaxy should send this in the form of the `GalaxyServerStatus` message with the current
values for the attributes in the message. Upon receipt of this message, if the galaxy is
trusted by the login server, the status changes will be immediately reflect on the login
server, and persisted in the galaxy cluster database.

### Connection Server Status
When players choose a galaxy upon which to play, they may not be connected directly to the
galaxy server, but a connection server. There may be more than one connection server node
servicing a galaxy. As these nodes come online and go offline, the galaxy server must inform
the login server.

When the galaxy server first identifies with the `GalaxyServerId` message, after receiving
acknowledgement of acceptance, in addition to the `GalaxyServerStatus` message, it should
follow with a `ConnectionServerOnline` message. This message may contain multiple connection
servers that are servicing the galaxy server. The login server will choose the least populated
connection server when choosing to connect a player.

Likewise, when a connection server becomes unavailable, whether that is because the galaxy
server is spinning down a node due to demand, the connection server crashed or became
unresponsive, or the galaxy server itself is shutting down, the galaxy server should inform
the login server of this change. This is accomplished by issuing the `ConnectionServerOffline`
message. This message may contain multiple connection servers as well.

### Login Server Offline
Occasionally, the login server may be brought offline for various reasons: scheduled maintenance,
updating, or unexpected crashes. If it is brought offline in a controlled fashion, then it will
gracefully communicate to all trusted galaxy clusters that it is going offline with the
`LoginServerOffline` message.

If the login server unexpectedly crashes, the process is terminated forcefully
(system reboot or killing the process), or network connectivity lost, then the
`LoginServerOffline` message may not be sent. However, when the process is restarted,
it will send the `LoginServerOnline` message to all trusted galaxies. If internet
connectivity is interrupted, then services should resume immediately upon the
connectivity being re-established with no further action being required. If galaxy
servers try to send communication to an unreachable login server, they should make note
and schedule the communication to retry later in case connectivity is restored.