export const metadata = {
  title: "Enterprise Sales",
  description: "Customer, catalogue, quote, order, and payment journeys",
};

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body style={{ fontFamily: "Segoe UI, sans-serif", margin: 0, background: "#0b1f33", color: "#f4f7fb" }}>
        {children}
      </body>
    </html>
  );
}
