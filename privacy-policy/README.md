# AbsForge Privacy Policy — Static Website & Render Deployment Guide

This directory contains the official, mobile-responsive static Privacy Policy website for the Android fitness application **AbsForge** (`com.absforge`).

---

## Files Included

- `index.html` — Semantic HTML5 Privacy Policy page styled with AbsForge visual identity (Obsidian Dark + Crimson Red accent).
- `styles.css` — Custom mobile-first responsive CSS styling with dark theme, high contrast, smooth scrolling, and print styles.
- `README.md` — Deployment and configuration guide for Render static hosting.

---

## Pre-Deployment Checklist

Before deploying to Render or your production server:

1. **Configure Your Support Email**:
   - Open `index.html`.
   - Search for `support@[YOUR-DOMAIN]`.
   - Replace it with your real contact support email (e.g. `support@absforge.app` or `contact@yourdomain.com`).

2. **Verify Mobile Responsiveness**:
   - Open `index.html` in your browser.
   - Resize to mobile widths (320px, 375px, 414px) and desktop widths to confirm formatting.

---

## How to Deploy on Render (Step-by-Step)

Render allows you to host static sites for free with automatic HTTPS/SSL and global CDN distribution.

### Option A: Deploying directly from a GitHub / GitLab Repository

1. **Push Files to GitHub/GitLab**:
   Ensure `index.html` and `styles.css` are committed to your repository.

2. **Log into Render**:
   - Go to [render.com](https://render.com) and log into your dashboard.

3. **Create a New Static Site**:
   - Click the **New +** button in the upper right.
   - Select **Static Site**.

4. **Connect Your Repository**:
   - Select your repository (`stitch_absforge_fitness_app_design` or your dedicated repository).

5. **Configure Deployment Settings**:
   - **Name**: `absforge-privacy-policy` (or your preferred site name)
   - **Branch**: `main` (or your default branch)
   - **Root Directory**: `privacy-policy` *(If this folder is located in a subfolder of your repo. If at repo root, leave blank)*
   - **Build Command**: Leave empty (or `echo "No build step required"`)
   - **Publish Directory**: `.` (or `privacy-policy` depending on root setting)

6. **Deploy Site**:
   - Click **Create Static Site**.
   - Render will build and publish your site in seconds.
   - Your live Privacy Policy URL will be generated (e.g. `https://absforge-privacy-policy.onrender.com`).

---

### Option B: Using a Render Infrastructure as Code (`render.yaml`) Blueprint

If you prefer automated blueprint deployment, add a `render.yaml` file to your repo root:

```yaml
services:
  - type: web
    name: absforge-privacy-policy
    env: static
    buildCommand: echo "Static site ready"
    staticPublishPath: ./privacy-policy
```

---

## Linking to Google Play Console

Once deployed, copy your live Render URL (or custom domain URL, e.g. `https://absforge.app/privacy-policy`) and paste it into:

1. **Google Play Console** &rarr; *App Content* &rarr; *Privacy Policy*.
2. **AbsForge Android App** (if updating external privacy link placeholders).
