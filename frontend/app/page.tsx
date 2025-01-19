import Link from "next/link";
import Header from './components/header';

export default function Home() {
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

        {/* Connect Section */}
        <div className="mt-12 flex flex-col items-center">
          <div className="bg-gray-800 p-6 rounded-lg shadow-xl w-80">
            <input
              type="text"
              placeholder="Enter IP Address"
              className="p-3 w-full mb-4 rounded-md bg-gray-700 text-white focus:outline-none focus:ring-2 focus:ring-pink-500 focus:border-transparent"
            />
            <Link href="/chat">
              <button className="neon-button px-6 py-3 rounded-md w-full text-lg font-semibold">
                Connect
              </button>
            </Link>
          </div>
        </div>
      </main>
    </div>
  );
}
