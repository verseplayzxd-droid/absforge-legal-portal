# AbsForge Legal & Privacy Web Portal — Render Deployment Guide

This directory (`website/`) contains the official static web portal for the Android fitness application **AbsForge** (`com.absforge`).

---

## Web Pages Included

- `index.html` — **AbsForge Legal Portal Home Page** linking to all legal documents.
- `privacy.html` — **Privacy Policy** (Detailed local storage, Google AdMob, and UMP consent disclosures).
- `terms.html` — **Terms of Service** (Terms, license, disclaimers, and user obligations).
- `disclaimer.html` — **Health & Fitness Disclaimer** (Medical warnings, exercise risk notices).
- `styles.css` — Shared responsive theme (Obsidian Dark + Crimson Red accent).
- `README.md` — Deployment guide for Render static hosting.

---

## Pre-Deployment Checklist

Before deploying:
1. Open `privacy.html`, `terms.html`, `disclaimer.html`, and `index.html`.
2. Replace `support@[YOUR-DOMAIN]` with your actual support email (e.g., `support@absforge.app` or `contact@yourdomain.com`).

---

## How to Deploy on Render (Step-by-Step)

Render allows you to host static sites for free with automatic HTTPS/SSL and global CDN distribution.

### Step 1: Push Repository to GitHub/GitLab
Ensure `website/` directory with `index.html`, `privacy.html`, `terms.html`, `disclaimer.html`, and `styles.css` is pushed to your GitHub or GitLab repository.

### Step 2: Create a New Static Site on Render
1. Log into your [Render Dashboard](https://render.com).
2. Click **New +** &rarr; Select **Static Site**.
3. Connect your repository (`stitch_absforge_fitness_app_design` or your custom repo).

### Step 3: Configure Settings
- **Name**: `absforge-legal-portal` (or your preferred name)
- **Branch**: `main`
- **Root Directory**: `website`
- **Build Command**: Leave blank (or `echo "Static site ready"`)
- **Publish Directory**: `.`

### Step 4: Deploy
Click **Create Static Site**. Your site will be live in seconds at a URL such as:
- `https://absforge-legal-portal.onrender.com`
- `https://absforge-legal-portal.onrender.com/privacy.html` (Privacy Policy)
- `https://absforge-legal-portal.onrender.com/terms.html` (Terms of Service)
- `https://absforge-legal-portal.onrender.com/disclaimer.html` (Health Disclaimer)

---

## Google Play Console Links

When filling out your **Google Play Console** store listing:
- **Privacy Policy URL**: `https://absforge-legal-portal.onrender.com/privacy.html`
- **App Website**: `https://absforge-legal-portal.onrender.com`
