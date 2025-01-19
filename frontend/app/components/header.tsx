import Link from 'next/link';

export default function Header() {
  return (
    <header className="flex justify-between items-center p-6 bg-gray-800 shadow-lg">
      <div className="flex items-center space-x-2">
        <img src="/logo.png" alt="App Icon" className="w-8 h-8" />
        <span className="text-2xl font-bold neon-text">HushHush</span>
      </div>
      <div className="space-x-6">
        <Link
          href="/"
          className="neon-text text-lg transition-transform transform hover:scale-105 hover:text-pink-500"
        >
          Home
        </Link>
        <Link
          href="/chat"
          className="neon-text text-lg transition-transform transform hover:scale-105 hover:text-pink-500"
        >
          Chat
        </Link>
      </div>
    </header>
  );
}
