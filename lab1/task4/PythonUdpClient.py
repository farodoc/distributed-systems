import socket

serverIP = "127.0.0.1"
serverPort = 9009
msg = "Ptext from python client"

print("PYTHON UDP CLIENT")
client = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
client.sendto(bytes(msg, "cp1250"), (serverIP, serverPort))
