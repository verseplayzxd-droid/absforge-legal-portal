---
name: AbsForge
colors:
  surface: '#121317'
  surface-dim: '#121317'
  surface-bright: '#38393d'
  surface-container-lowest: '#0d0e12'
  surface-container-low: '#1a1b1f'
  surface-container: '#1e1f23'
  surface-container-high: '#292a2e'
  surface-container-highest: '#343539'
  on-surface: '#e3e2e7'
  on-surface-variant: '#c3caac'
  inverse-surface: '#e3e2e7'
  inverse-on-surface: '#2f3034'
  outline: '#8d9479'
  outline-variant: '#434933'
  surface-tint: '#a6d700'
  primary: '#ffffff'
  on-primary: '#273500'
  primary-container: '#bef500'
  on-primary-container: '#536d00'
  inverse-primary: '#4e6700'
  secondary: '#c9c6c5'
  on-secondary: '#313030'
  secondary-container: '#4a4949'
  on-secondary-container: '#bab8b7'
  tertiary: '#ffffff'
  on-tertiary: '#303032'
  tertiary-container: '#e4e2e4'
  on-tertiary-container: '#656466'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#bef500'
  primary-fixed-dim: '#a6d700'
  on-primary-fixed: '#151f00'
  on-primary-fixed-variant: '#3a4d00'
  secondary-fixed: '#e5e2e1'
  secondary-fixed-dim: '#c9c6c5'
  on-secondary-fixed: '#1c1b1b'
  on-secondary-fixed-variant: '#474646'
  tertiary-fixed: '#e4e2e4'
  tertiary-fixed-dim: '#c8c6c8'
  on-tertiary-fixed: '#1b1b1d'
  on-tertiary-fixed-variant: '#474649'
  background: '#121317'
  on-background: '#e3e2e7'
  surface-variant: '#343539'
typography:
  display-lg:
    fontFamily: Inter
    fontSize: 48px
    fontWeight: '800'
    lineHeight: 56px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.01em
  headline-lg-mobile:
    fontFamily: Inter
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 36px
  title-md:
    fontFamily: Inter
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-sm:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-caps:
    fontFamily: JetBrains Mono
    fontSize: 12px
    fontWeight: '700'
    lineHeight: 16px
    letterSpacing: 0.1em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  container-margin: 20px
  gutter: 16px
  stack-sm: 8px
  stack-md: 16px
  stack-lg: 32px
---

## Brand & Style
The design system is engineered for a high-performance fitness environment. It targets a professional, disciplined demographic that values precision and energy. The aesthetic is **Modern Android**, leaning into a dark-mode-first experience that reduces eye strain in low-light gym environments while emphasizing critical data.

The style leverages **High-Contrast Minimalism** with **Glassmorphic** accents. It utilizes deep, immersive backgrounds to make "Electric Lime" action points vibrate with energy. The emotional response is one of focus, power, and momentum—designed to make the user feel like they are interacting with a high-end piece of athletic equipment rather than just an app.

## Colors
The palette is centered on a "Void and Spark" philosophy. 
- **Primary (Electric Lime):** Reserved exclusively for active states, progress indicators, and primary call-to-action buttons. It signifies movement and completion.
- **Background (Deep Charcoal):** A near-black (#0A0A0A) base that provides infinite depth.
- **Surface (Elevated Gray):** A slightly lighter gray (#1C1C1E) used for cards and grouped content to create visual hierarchy.
- **Typography:** Pure white for high-readability headlines; Soft Gray (#8E8E93) for metadata and inactive states to maintain focus on primary metrics.

## Typography
The system uses **Inter** for its clean, Swiss-style precision and exceptional legibility at small sizes. For data-heavy metrics (reps, sets, timers), **JetBrains Mono** is introduced to provide a technical, "instrument-panel" feel that distinguishes data from instructional text.

Headlines should use tight letter spacing and heavy weights to convey strength. Body text remains open and legible. Use `label-caps` for section headers and categorization to maintain a disciplined, organized structure.

## Layout & Spacing
This design system follows an **8dp grid system** optimized for edge-to-edge Android layouts. 

- **Grid:** Use a 4-column grid for mobile and an 8-column grid for tablets. 
- **Margins:** Standard side margins are set to 20px to allow content to feel expansive while maintaining a "safe zone" for thumb interaction.
- **Safe Areas:** Adhere to Android's system bars (Status and Navigation). Content should flow behind the navigation bar using transparent system UI styling for a modern, immersive look.
- **Vertical Rhythm:** Use 32px spacing between major card groups and 16px within card content.

## Elevation & Depth
Depth is created through **Tonal Layering** rather than traditional shadows. Since the background is near-black, standard shadows are invisible. 

- **Level 0 (Base):** #0A0A0A (The canvas).
- **Level 1 (Cards):** #1C1C1E with a 1px subtle stroke (#FFFFFF at 5% opacity) to define edges.
- **Level 2 (Modals/Popovers):** #2C2C2E with a subtle backdrop blur (20px) on the layers beneath.
- **Accents:** Use a subtle outer glow (Electric Lime at 20% opacity) for active progress rings or high-intensity metrics to simulate a light-emitting diode (LED) effect.

## Shapes
The shape language is defined by large, comfortable radii that contrast with the aggressive color palette.
- **Primary Container Radius:** 24px (used for main workout cards and dashboard widgets).
- **Button Radius:** 16px or fully pill-shaped (32px+) for primary actions.
- **Inner Elements:** 8px for smaller items like input fields or nested chips within a card.
- **Progress Rings:** Always use rounded stroke caps to maintain the soft-geometry aesthetic.

## Components
- **Primary Action Button:** Background is Electric Lime Green (#C6FF00), text is Black (#0A0A0A) in Bold. Use a 24px height padding.
- **Workout Cards:** Background #1C1C1E, 24px corner radius. Headlines in White, sub-metrics in Soft Gray. Use 1px "ghost borders" for definition.
- **Progress Rings:** Thick 8-12pt strokes. The "track" is dark gray (#2C2C2E), and the "progress" is a gradient from Electric Lime to a slightly darker shade of lime.
- **Inputs:** Dark background, 1px Soft Gray border that turns Electric Lime on focus. Label text stays in JetBrains Mono above the field.
- **Chips:** Small, rounded (12px) containers for exercise tags (e.g., "Strength", "HIIT"). Use a semi-transparent white background (10% opacity) with white text.
- **Navigation:** Use a Bottom Navigation Bar with a blur effect. The active icon receives a small Electric Lime dot indicator underneath.