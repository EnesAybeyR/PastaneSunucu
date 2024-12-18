#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <winsock2.h>
#pragma comment(lib, "ws2_32.lib")

#define PORT 5000
#define MAX_BUFFER 1024

void error(const char *msg) {
    perror(msg);
    exit(1);
}

int main() {
    WSADATA wsaData;
    SOCKET clientSocket;
    struct sockaddr_in serverAddr;
    char buffer[MAX_BUFFER];

  
    if (WSAStartup(MAKEWORD(2, 2), &wsaData) != 0) {
        error("Winsock initialization failed");
    }

   
    clientSocket = socket(AF_INET, SOCK_STREAM, 0);
    if (clientSocket == INVALID_SOCKET) {
        error("Socket creation failed");
    }

  
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(PORT);
    serverAddr.sin_addr.s_addr = inet_addr("127.0.0.1");

    
    if (connect(clientSocket, (struct sockaddr*)&serverAddr, sizeof(serverAddr)) < 0) {
        error("Connection failed");
    }

    printf("Connected to the server.\n");

    while (1) {
       
        memset(buffer, 0, MAX_BUFFER);
        recv(clientSocket, buffer, sizeof(buffer), 0);
        printf("%s", buffer);

       
        fgets(buffer, MAX_BUFFER, stdin);
        send(clientSocket, buffer, strlen(buffer), 0);

       
        if (strncmp(buffer, "e", 1) == 0) {
            break;
        }


        memset(buffer, 0, MAX_BUFFER);
        recv(clientSocket, buffer, sizeof(buffer), 0);
        printf("%s\n", buffer);
    }


    closesocket(clientSocket);
    WSACleanup();

    return 0;
}

