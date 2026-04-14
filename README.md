# Technicolor Server 

I built this project to understand how client-server communication actually works using sockets.

At a high level, the server holds a few files, and the client can:
- list all available files
- open and read any one of them
- navigate to directories

---

## How it works

I designed a very simple protocol:

- `LIST` → server sends back all file names  
- `OPEN <filename>` → server sends the content of that file
- `PWD` -> server return the present working directory.
- `CD` -> navigate across directories.

Each response ends with `END` so the client knows when to stop reading.

---

## What I learned

This project started as something “simple”, but turned out to be a great learning experience.

- Data is sent as bytes, not neat “messages”
- Even something like `\n` matters a lot when reading data
- If you don’t define boundaries properly, the program just hangs
- Handling edge cases (extra spaces, wrong input, missing files) is not optional
- Writing your own protocol makes you appreciate why they exist in the first place

---

## How to run

1. Open terminal in project folder.
2. Run server

javac Server.java
java Server

3. Open another terminal and run client:

javac Client.java
java Client

4. Type commands like:
   LIST
   OPEN orange.txt
