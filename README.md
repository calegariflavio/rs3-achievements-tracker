# RS3 Achievements Tracker — Frontend

React SPA for tracking RuneScape 3 player achievements, hiscores, and archaeology logs.

## Tech Stack

- **React 19** + **TypeScript**
- **Vite 8** (bundler + dev server)
- **Tailwind CSS 4**
- **React Router 7**
- **Axios**

## Getting Started

### Prerequisites

- Node.js 20+
- The [backend](https://github.com/calegariflavio/rs3-achievements-tracker-backend) running on `http://localhost:8080`

### Install & Run

```bash
npm install
npm run dev
```

The app starts on `http://localhost:5173`.

### Environment Variables

Create a `.env` file to override the API base URL:

```env
VITE_API_URL=http://localhost:8080
```

> The default points to `localhost:8080`. Change this for staging/production.

## Scripts

| Command           | Description                        |
|-------------------|------------------------------------|
| `npm run dev`     | Start dev server with HMR          |
| `npm run build`   | Type-check + production build      |
| `npm run preview` | Preview the production build       |
| `npm run lint`    | Run ESLint                         |

## Project Structure

```
src/
├── api/          # Axios instance and API call functions
├── assets/       # Images and skill icons
├── components/   # Shared UI components (Navbar, Footer, ProtectedRoute)
├── context/      # AuthContext for JWT auth state
├── data/         # Static data (archaeology items)
└── pages/        # Route-level page components
```

## Pages

| Route              | Description                        |
|--------------------|------------------------------------|
| `/`                | Home / landing page                |
| `/player/:name`    | Player profile & hiscores          |
| `/archaeology`     | Archaeology log tracker (auth)     |
| `/login`           | Login page                         |
| `/about`           | About page                         |

## License

MIT
