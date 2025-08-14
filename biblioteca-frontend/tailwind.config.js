/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        brand: {
          50: '#f0f7ff',
          100: '#e0efff',
          200: '#b9dbff',
          300: '#8ac1ff',
          400: '#5aa6ff',
          500: '#2b8cff',
          600: '#1673e6',
          700: '#125abd',
          800: '#0f4693',
          900: '#0b336b'
        }
      },
      boxShadow: { soft: '0 10px 25px -10px rgba(37,99,235,.25)' },
      borderRadius: { '2xl': '1rem' }
    },
  },
  plugins: [],
}
