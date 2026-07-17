---
name: Vibrant EdTech
colors:
  surface: '#fff8f7'
  surface-dim: '#f1d3d4'
  surface-bright: '#fff8f7'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#fff0f0'
  surface-container: '#ffe9e9'
  surface-container-high: '#ffe1e2'
  surface-container-highest: '#fadbdc'
  on-surface: '#271718'
  on-surface-variant: '#5b4041'
  inverse-surface: '#3e2c2d'
  inverse-on-surface: '#ffeced'
  outline: '#8f6f70'
  outline-variant: '#e4bdbe'
  surface-tint: '#bc0c36'
  primary: '#bc0b36'
  on-primary: '#ffffff'
  primary-container: '#e02f4c'
  on-primary-container: '#140002'
  inverse-primary: '#ffb3b5'
  secondary: '#a13c45'
  on-secondary: '#ffffff'
  secondary-container: '#fb8188'
  on-secondary-container: '#721925'
  tertiary: '#006b54'
  on-tertiary: '#ffffff'
  tertiary-container: '#00876a'
  on-tertiary-container: '#000604'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#ffdada'
  primary-fixed-dim: '#ffb3b5'
  on-primary-fixed: '#40000c'
  on-primary-fixed-variant: '#920026'
  secondary-fixed: '#ffdada'
  secondary-fixed-dim: '#ffb3b5'
  on-secondary-fixed: '#40000b'
  on-secondary-fixed-variant: '#82242f'
  tertiary-fixed: '#63fbcf'
  tertiary-fixed-dim: '#3edeb3'
  on-tertiary-fixed: '#002117'
  on-tertiary-fixed-variant: '#00513e'
  background: '#fff8f7'
  on-background: '#271718'
  surface-variant: '#fadbdc'
typography:
  display-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 48px
    fontWeight: '700'
    lineHeight: '1.2'
    letterSpacing: -0.02em
  display-lg-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 32px
    fontWeight: '700'
    lineHeight: '1.2'
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 32px
    fontWeight: '700'
    lineHeight: '1.3'
  headline-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 24px
    fontWeight: '600'
    lineHeight: '1.4'
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 18px
    fontWeight: '400'
    lineHeight: '1.6'
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: '1.6'
  label-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '600'
    lineHeight: '1'
    letterSpacing: 0.01em
  label-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '500'
    lineHeight: '1'
    letterSpacing: 0.05em
rounded:
  sm: 0.5rem
  DEFAULT: 1rem
  md: 1.5rem
  lg: 2rem
  xl: 3rem
  full: 9999px
spacing:
  unit: '8'
  container-max: '1200'
  gutter: '24'
  margin-mobile: '16'
  section-gap: '80'
---

## Brand & Style

The design system is built to balance educational authority with a sophisticated, high-energy tech aesthetic. It targets ambitious language learners who value modern AI-driven tools. The visual narrative is defined by a **Corporate / Modern** structure infused with **Glassmorphism** and vibrant warm gradients to create an atmosphere of focused innovation and progress.

The UI should feel:
- **Dynamic:** Through the use of active rose-to-pink gradients and fluid transitions.
- **Trustworthy:** Utilizing structured layouts and high-contrast typography.
- **Approachable:** Softened by rounded corners and spacious, airy layouts with warm neutral undertones.

## Colors

The palette is centered around a high-vibrancy rose-to-muted-pink gradient, now accented by a bright, energetic mint-teal.

- **Primary & Secondary:** These energetic rose tones are used for core branding, key action buttons, and hero elements to establish a sense of passion and energy.
- **Tertiary:** The bright mint-teal acts as a high-visibility accent and highlight color, used for secondary interactive elements and signaling growth and fresh progress.
- **Gradients:** Use a linear gradient (typically 135 degrees) from `primary` to `secondary`. This is the signature brand element for hero sections and primary buttons.
- **Neutral:** A warm, taupe-tinted base ensures readability and a clean, comfortable educational environment.

## Typography

This design system utilizes **Plus Jakarta Sans** for its friendly, open apertures and professional geometric structure. It bridges the gap between a tech startup and an educational institution.

- **Headlines:** Use Bold (700) weights with slightly tighter letter-spacing to create a strong visual anchor.
- **Body:** Use Regular (400) weights with generous line height to ensure comfortable reading of educational content.
- **Hierarchy:** High contrast in size between display text and body text is encouraged to guide the user through marketing funnels.

## Layout & Spacing

The layout follows a **Fixed Grid** model for desktop to maintain a premium, editorial feel, while transitioning to a **Fluid Grid** for mobile.

- **Grid:** A 12-column grid is used for desktop. 
- **Sectioning:** Large vertical gaps (`section-gap`) are essential to separate the different apps in the suite and the social proof sections (awards/testimonials).
- **Rhythm:** All spacing (padding, margins, gaps) should be multiples of the 8px base unit.

## Elevation & Depth

Hierarchy is established through **Tonal Layers** and **Glassmorphism**.

- **Surfaces:** Use warm white cards on subtle taupe or rose-tinted backgrounds.
- **Glassmorphism:** Navigation bars and hero overlays should use a background blur (12px to 20px) with a semi-transparent warm white (80%) or rose (40%) fill.
- **Shadows:** Avoid heavy, dark shadows. Use ambient, colored shadows that inherit a slight tint from the primary rose to maintain a "glowing" effect.
- **Hero Depth:** Layer device mockups (phones/tablets) with soft drop shadows to create a sense of physical interaction.

## Shapes

The design system uses a highly rounded, **Pill-shaped** language to maximize friendliness and modern appeal.

- **Buttons:** Always use full pill-shape (radius: 100px) for primary actions.
- **Cards:** Use large, soft corners (`rounded-xl` or 24px+) to contain app features and testimonials.
- **Icons:** Enclose social and platform icons in circular containers to maintain the soft visual theme.

## Components

### Buttons
- **Primary:** Pill-shaped with the brand gradient background and white text. Include a subtle hover lift effect.
- **Secondary/Outline:** Pill-shaped with a 2px rose border and rose text. 
- **Ghost:** Warm neutral text with no border, used for navigation items on colored backgrounds.

### Cards
- **Feature Cards:** Large corner radius (24px), warm white background, and high-contrast rose headlines. 
- **Award Cards:** Minimalist styling with a focus on the badge/logo. Use a very subtle taupe border (1px) to define boundaries on white backgrounds.

### Input Fields
- Rounded (radius 12px) with a light taupe stroke. On focus, the stroke should transition to the primary rose.

### App Store Badges
- Standardized black badges for App Store, Google Play, and Microsoft Store, consistently aligned in a row with equal spacing.

### Chips & Tags
- Used for "New" features or categories. Small, pill-shaped with light mint backgrounds and deep teal/mint (`tertiary`) text for a fresh, high-visibility look.