# 🎨 Artsy — Multi-Platform Artist Discovery App

**Artsy** is a full-stack, multi-platform application designed to help users discover artists, explore their artworks and biographies, and save their favorites—all through an elegant and responsive user experience. Built with a shared **Node.js + Express** backend, it powers both a **React web app** and a native **Jetpack Compose Android app**, maintaining consistent data flow and user state across platforms.

The application integrates seamlessly with the [Artsy API](https://developers.artsy.net/v2/) to retrieve rich artist data and media, while leveraging **MongoDB Atlas** for persistent user data and favorites.

---

## 🌐 Live Deployments

- 🖥️ **Web App**: [Visit Website](https://your-live-site.com)
- 📱 **Android App**: [Watch Demo Video](demo/demo.mov)

---

## 📦 Tech Stack

| Layer        | Technologies Used                                  |
|--------------|----------------------------------------------------|
| Frontend     | React + TypeScript + Bootstrap                     |
| Backend      | Node.js + Express + MongoDB Atlas                  |
| Mobile App   | Android (Jetpack Compose, Retrofit, Material3)     |
| API          | Artsy REST API v2                                  |
| Auth & User  | JWT, Cookies, Gravatar Avatar Integration          |
| Deployment   | Google Cloud Platform                              |

---

## 🧠 Project Architecture

This project follows a **modular, cross-platform architecture**:
- 🔁 **Shared Backend**: Both web and mobile clients interact with a single Express API server, ensuring consistent session management and data.
- 🧱 **Decoupled Clients**: The React frontend and Android app were built independently, enabling future expansion to other platforms like iOS or desktop.

