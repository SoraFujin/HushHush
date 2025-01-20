"use client";

import { useState } from "react";
import Link from "next/link";
import Header from "./components/header";

export default function Home() {
  const [serverIP, setServerIP] = useState("");
  const [connectionMessage, setConnectionMessage] = useState("");

  const handleConnect = async () => {
    if (serverIP.trim() === "") {
      setConnectionMessage("Please enter a valid IP address.");
      return;
    }

    try {
      const response = await fetch("/api/chat/connect", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ serverIP }),
      });

      const result = await response.text(); // Or `response.json()` if the backend returns JSON
      setConnectionMessage(result);
    } catch (error) {
      setConnectionMessage("Failed to connect to the server. Please try again.");
    }
  };

  return (
    <div className="bg-gray-900 text-white">
      {/* Header */}
      <Header />

      {/* Main Container */}
      <main className="flex flex-col justify-center items-center mt-16">
        <div className="max-w-3xl text-center p-6 bg-gray-800 rounded-lg shadow-xl">
          <h1 className="text-4xl font-bold text-center neon-gradient neon-effect">
            HushHush
          </h1>

          <p className="text-lg mb-6">
            With HushHush, your messages are kept completely private. We use
            Diffie-Hellman for secure key exchange, meaning only you and the
            person you're talking to can access the secret key needed to encrypt
            your conversations. Then, using one-time padding encryption, your
            messages are further protected, making them unreadable to anyone
            else. The best part? HushHush is a peer-to-peer (P2P) app, which
            means no central server stores your messages—everything is
            decentralized for added privacy and security. Whether you’re sending
            a funny joke or an important message, HushHush ensures your
            conversations stay safe, and no one can spy on them. In a world full
            of distractions, HushHush is your quiet, safe space to talk.
          </p>
        </div>

        <div className="mt-4">
          <Link href="/chat">
            <button className="px-6 py-3 bg-blue-500 text-white rounded-md">
              Go to Chat
            </button>
          </Link>
        </div>
      </main>
    </div>
  );
}
