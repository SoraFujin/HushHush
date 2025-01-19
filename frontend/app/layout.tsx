import type { Metadata } from "next";
import { Poppins } from "next/font/google";
import "./globals.css";
import Head from "next/head";

// Import Poppins font
const poppins = Poppins({
  weight: ["400", "500", "600", "700"],  // You can add or remove weights as needed
  subsets: ["latin"],
  variable: "--font-poppins",  // Optional: to use custom CSS variables
});

export const metadata: Metadata = {
  title: "HushHush",
  description:
    "HushHush ensures your messages stay private with secure Diffie-Hellman key exchange and one-time padding encryption.",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <Head>
        <link rel="icon" href="/logo.png" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
      </Head>
      <body
        className={`${poppins.variable} antialiased bg-gray-900 text-white`}
      >
        {children}
      </body>
    </html>
  );
}
