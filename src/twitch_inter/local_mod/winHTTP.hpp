#pragma once

#ifndef WINHTTP_HPP
#define WINHTTP_HPP

#define BUFFER_SIZE 1024


#include <windows.h>
#include <vector>
#include <string>
#include <stdio.h>

using namespace std;



#pragma comment(lib, "ws2_32.lib")

class WinHTTP {
    public:
    char readBuffer[BUFFER_SIZE];
    char sendBuffer[BUFFER_SIZE];
    char tmpBuffer[BUFFER_SIZE];

    HINSTANCE hInst;
    WSADATA wsaData;
    
    void mParseUrl(char* murl, string& host, string& filepath, string& filename)
    {
        string::size_type n;
        string url = murl;
        if (url.substr(0, 7) == "http://")
            url.erase(0, 7);

        if (url.substr(0, 8) == "https://")
            url.erase(0, 8);

        n = url.find("/");

        if (n != string::npos)
        {
            host = url.substr(0, n);
            filepath = url.substr(n);
            n = filepath.find("/");
            filename = filepath.substr(n+1);
        }
        else 
        {
            host = url;
            filepath = "/";
            filename = "";
        }
    }

    SOCKET connectToServer(char* szServerName, WORD portNum)
    {
        struct hostent *hp;
        unsigned int addr;
        struct sockaddr_in server;
        SOCKET conn;

        conn = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
        if (conn == INVALID_SOCKET)
        {
            printf("socket() failed with error %d\n", WSAGetLastError());
            return NULL;
        }

        if (inet_addr(szServerName) == INADDR_NONE)
            hp = gethostbyname(szServerName);
        else {
            addr = inet_addr(szServerName);
            hp = gethostbyaddr((char *)&addr, sizeof(addr), AF_INET);
        }

        if (!hp) {
            printf("gethostbyname() failed with error %d\n", WSAGetLastError());
            closesocket(conn);
            return NULL;
        }

        server.sin_addr.s_addr= *((unsigned long*)hp->h_addr);
        server.sin_family = AF_INET;
        server.sin_port = htons(portNum);

        if (connect(conn, (struct sockaddr *)&server, sizeof(server)) == SOCKET_ERROR)
        {
            printf("connect() failed with error %d\n", WSAGetLastError());
            closesocket(conn);
            return NULL;
        }

        return conn;
    }

    int getHeaderLength(char *content) {
        const char *srchStr1 = "\r\n\r\n", *srchStr2 = "\n\r\n\r";
        char *findPos;
        int ofset = -1;

        findPos = strstr(content, srchStr1);
        if (findPos != NULL){
            ofset = findPos - content;
            ofset += strlen(srchStr1);
        } else {
            findPos = strstr(content, srchStr2);
            if (findPos != NULL){
                ofset = findPos - content;
                ofset += strlen(srchStr2);
            }
        }
        return ofset;
    }

    void _setHeader(char* data) {
        if (data == NULL)
            return;

        size_t len = strlen(data);
        sprintf(this->tmpBuffer, data);
        strcpy(this->sendBuffer, this->tmpBuffer);
        strcpy(this->sendBuffer, "\r\n");
    }

    void _headerEnd() {
        strcpy(this->sendBuffer, "\r\n");
    }

    void _resetBuffer() {
        memset(this->readBuffer, 0, BUFFER_SIZE);
        memset(this->sendBuffer, 0, BUFFER_SIZE);
        memset(this->tmpBuffer,  0, BUFFER_SIZE);
    }

    char *doGET(char *szUrl, long &bytesReturnedOut, char **headerOut, int port=8080) {
        char tmp[BUFFER_SIZE];
        char *tmpResult=NULL, *result;
        SOCKET conn;
        string server, filepath, filename;
        long totalBytesRead, thisReadSize, headerLen;

        this->mParseUrl(szUrl, server, filepath, filename);

        conn = connectToServer((char*)server.c_str(), port);

        sprintf(tmp, "GET %s HTTP/1.0", filepath.c_str());
        this->_setHeader(tmp);
        sprintf(tmp, "Host: %s", server.c_str());
        this->_setHeader(tmp);
        this->_headerEnd();

        send(conn, this->sendBuffer, strlen(this->sendBuffer), 0);

        printf("Buffer being sent: \n%s\n", this->sendBuffer);

        totalBytesRead = 0;
        while (1) {
            memset(this->readBuffer, 0, BUFFER_SIZE);
            thisReadSize = recv(conn, this->readBuffer, BUFFER_SIZE, 0);
            if (thisReadSize <= 0)
                break;
            
            tmpResult = (char*)realloc(tmpResult, thisReadSize+totalBytesRead);

            memcpy(tmpResult+totalBytesRead, this->readBuffer, thisReadSize);
            totalBytesRead += thisReadSize;
        }

        headerLen = this->getHeaderLength(tmpResult);
        long contentLen = totalBytesRead - headerLen;
        memcpy(result, tmpResult+headerLen, contentLen);
        result[contentLen] = '\0';
        char *myTmp;

        myTmp = new char[contentLen+1];
        strncpy(myTmp, tmpResult, headerLen);
        myTmp[headerLen] = NULL;
        delete(tmpResult);
        *headerOut = myTmp;

        bytesReturnedOut = contentLen;
        closesocket(conn);
        this->_resetBuffer();
        return (result);
    }
    WinHTTP() = default;
};
#endif