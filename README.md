# Artsy — Multi-Platform Artist Discovery App
---

## Live Deployments

- **Web App**: [Visit Website](https://react-frontend-1087721607794.us-central1.run.app)
- **Android App**: [Watch Demo Video](https://drive.google.com/file/d/1Lv3oxTVlghkXxhB2ZDtLjjq7Ux63rq74/view?usp=sharing)

---

## Tech Stack

| Layer        | Technologies Used                                  |
|--------------|----------------------------------------------------|
| Frontend     | React + TypeScript + Bootstrap                     |
| Backend      | Node.js + Express + MongoDB Atlas                  |
| Mobile App   | Android (Jetpack Compose, Retrofit, Material3)     |
| API          | Artsy REST API v2                                  |
| Auth & User  | JWT, Cookies, Gravatar Avatar Integration          |
| Deployment   | Google Cloud Platform, Docker                      |

---

## Project Architecture

This project follows a **modular, cross-platform architecture**:
- **Shared Backend**: Both web and mobile clients interact with a single Express API server, ensuring consistent session management and data.
- **Decoupled Clients**: The React frontend and Android app were built independently, enabling future expansion to other platforms like iOS or desktop.

