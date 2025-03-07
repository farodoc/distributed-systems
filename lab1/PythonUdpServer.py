import socket;

serverPort = 9009
serverSocket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
serverSocket.bind(('', serverPort))
buff = []

print('PYTHON UDP SERVER')

while True:

    buff, address = serverSocket.recvfrom(1024)

    text = str(buff, 'cp1250')

    if text[0] == 'P':
        print("python udp server received msg from python client: " + text[1:])
    else:
        print("python udp server received msg from java client: " + text[1:])
