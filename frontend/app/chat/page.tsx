"use client";
import { useState } from "react";
import Header from "../components/header";

type Message = {
  text: string;
  sender: "user" | "other";
};

export default function Chat() {
  const [message, setMessage] = useState(""); // State for the input message
  const [messages, setMessages] = useState<Message[]>([]); // Explicit type for messages
  const [serverIP, setServerIP] = useState(""); // State for the server IP
  const [isConnected, setIsConnected] = useState(false); // State to track connection status
  const [sharedKey, setSharedKey] = useState(""); // State to store the shared key
  const [encryptedInput, setEncryptedInput] = useState(""); // State to store encrypted message input
  const [decryptedMessage, setDecryptedMessage] = useState(""); // State to store decrypted message

  const apiBaseUrl = "http://localhost:8080/api/chat";

  const handleStartServer = async () => {
    try {
      const response = await fetch(`${apiBaseUrl}/start-server`, {
        method: "POST",
      });
      const result = await response.text();
      alert(result);
    } catch (error) {
      console.error("Error starting server:", error);
      alert("Failed to start the server.");
    }
  };

  const handleConnectToServer = async () => {
    if (!serverIP.trim()) {
      alert("Please enter a valid IP address.");
      return;
    }

    const ipRegex = /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;
    if (!ipRegex.test(serverIP)) {
      alert("Please enter a valid IP address.");
      return;
    }

    setIsConnected(true);

    try {
      const response = await fetch(`${apiBaseUrl}/connect?serverIP=${serverIP}`, {
        method: "POST",
      });
      const result = await response.text();
      alert(result);
    } catch (error) {
      console.error("Error connecting to server:", error);
      alert("Failed to connect to server.");
    } finally {
      setIsConnected(false);
    }
};

  const handleSendMessage = async () => {
    if (!message.trim()) return;

    try {
      const response = await fetch(`${apiBaseUrl}/send-message`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ message }),
      });
      const encryptedMessage = await response.text();

      setMessages((prev) => [
        ...prev,
        { text: encryptedMessage, sender: "user" },
      ]);

      setMessage("");
      handleReceiveMessage(encryptedMessage);
    } catch (error) {
      console.error("Error sending message:", error);
      alert("Failed to send message.");
    }
  };

  const handleReceiveMessage = async (encryptedMessage: string) => {
    try {
      const response = await fetch(`${apiBaseUrl}/receive-message`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ encryptedMessage }),
      });
      const decryptedMessage = await response.text();

      setMessages((prev) => [
        ...prev,
        { text: decryptedMessage, sender: "other" },
      ]);
    } catch (error) {
      console.error("Error receiving message:", error);
      alert("Failed to receive message.");
    }
  };

  const handleShowSharedKey = async () => {
    try {
      const response = await fetch(`${apiBaseUrl}/get-shared-key`, {
        method: "GET", // Correcting to GET method
      });
      const key = await response.text();
      setSharedKey(key);
    } catch (error) {
      console.error("Error fetching shared key:", error);
      alert("Failed to fetch shared key.");
    }
  };

  const handleDecryptMessage = async () => {
    if (!encryptedInput.trim() || !sharedKey.trim()) return;

    try {
      const response = await fetch(`${apiBaseUrl}/decrypt-message`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ encryptedMessage: encryptedInput, sharedKey }),
      });
      const decrypted = await response.text();
      setDecryptedMessage(decrypted); // Set the decrypted message
    } catch (error) {
      console.error("Error decrypting message:", error);
      alert("Failed to decrypt message.");
    }
  };

  return (
    <>
      <Header />
      <div className="min-h-screen bg-gray-900 text-white p-4">
        <div className="flex justify-between items-center p-4 bg-gray-800 rounded-md">
          <h2 className="text-xl font-bold">Chat</h2>
        </div>

        <div className="mt-4 flex">
          <button
            onClick={handleStartServer}
            className="p-2 bg-blue-500 rounded-lg mr-2"
          >
            Start Server
          </button>
          <input
            type="text"
            value={serverIP}
            onChange={(e) => setServerIP(e.target.value)}
            placeholder="Enter Server IP"
            className="p-2 rounded-lg bg-gray-600 text-white flex-grow mr-2"
          />
          <button
            onClick={handleConnectToServer}
            className="p-2 bg-green-500 rounded-lg"
            disabled={isConnected}
          >
            {isConnected ? "Connecting..." : "Connect"}
          </button>
        </div>

        <div className="mt-4 space-y-4 overflow-y-auto max-h-96 p-4 bg-gray-800 rounded-md">
          {messages.map((msg, index) => (
            <div
              key={index}
              className={`flex ${
                msg.sender === "user" ? "justify-end" : "justify-start"
              }`}
            >
              <div
                className={`p-2 rounded-lg max-w-xs ${
                  msg.sender === "user" ? "bg-blue-500" : "bg-green-500"
                }`}
              >
                {msg.text}
              </div>
            </div>
          ))}
        </div>

        <div className="mt-4 flex">
          <input
            type="text"
            value={message}
            onChange={(e) => setMessage(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && handleSendMessage()}
            placeholder="Type a message..."
            className="w-full p-2 rounded-lg bg-gray-600 text-white"
          />
          <button
            onClick={handleSendMessage}
            className="ml-2 p-2 bg-blue-500 rounded-lg"
          >
            Send
          </button>
        </div>

        {/* Shared Key and Decryption Section */}
        <div className="mt-8 p-4 bg-gray-800 rounded-md">
          <div className="flex justify-between items-center">
            <h3 className="text-lg font-bold">Shared Key and Decryption</h3>
            <button
              onClick={handleShowSharedKey}
              className="p-2 bg-yellow-500 rounded-lg"
            >
              Show Shared Key
            </button>
          </div>

          <div className="mt-4 space-y-4">
            {sharedKey && (
              <div>
                <p className="text-white">Shared Key: {sharedKey}</p>
              </div>
            )}

            <div>
              <input
                type="text"
                value={encryptedInput}
                onChange={(e) => setEncryptedInput(e.target.value)}
                placeholder="Enter encrypted message"
                className="w-full p-2 rounded-lg bg-gray-600 text-white"
              />
            </div>
            <button
              onClick={handleDecryptMessage}
              className="mt-2 p-2 bg-purple-500 rounded-lg"
            >
              Decrypt Message
            </button>

            {decryptedMessage && (
              <div className="mt-2 p-2 bg-gray-700 rounded-lg">
                <p className="text-white">Decrypted Message: {decryptedMessage}</p>
              </div>
            )}
          </div>
        </div>
      </div>
    </>
  );
}