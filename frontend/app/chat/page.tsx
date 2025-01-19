"use client";

import { useState } from "react";
import Header from "../components/header";

export default function Chat() {
  const [message, setMessage] = useState("");
  const [messages, setMessages] = useState([
    { text: "Hello!", sender: "other" },
    { text: "Hi, how are you?", sender: "user" },
    { text: "I am good, thanks!", sender: "other" },
  ]);

  const handleSendMessage = () => {
    if (message.trim()) {
      setMessages([...messages, { text: message, sender: "user" }]);
      setMessage("");
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter") {
      e.preventDefault(); // Prevent the default Enter behavior (new line in text area)
      handleSendMessage(); // Send the message
    }
  };

  return (
    <>
      {/* Header */}
      <Header />
      <div className="min-h-screen bg-gray-900 text-white p-4">
        {/* Chat Header */}
        <div className="flex justify-between items-center p-4 bg-gray-800 rounded-md">
          <h2 className="text-xl font-bold">Chat</h2>
        </div>

        {/* Chat Messages */}
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

        {/* Message Input */}
        <div className="mt-4 flex">
          <input
            type="text"
            value={message}
            onChange={(e) => setMessage(e.target.value)}
            onKeyDown={handleKeyDown} // Add the onKeyDown event
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
      </div>
    </>
  );
}
