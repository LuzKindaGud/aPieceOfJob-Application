# Android Frontend Design System — Cyberpunk Dark Theme
# Jetpack Compose · Kotlin · Android Studio

> **Aesthetic**: Cyberpunk / Sci-Fi Admin Panel  
> **Theme**: Dark-first, neon accents, glassmorphism, animated depth  
> **Inspired by**: HKTstore Admin Panel — Orbitron + Rajdhani + Indigo/Purple/Cyan neon

---

## 1. Overall Theme and Style

- **Dark-first** — there is no light theme. Every screen uses the deep navy background.
- **Cyberpunk aesthetic** — neon glows, glassmorphism cards, animated grid backgrounds, octagonal clip-path shapes.
- **Typography-driven** — `Orbitron` for all headings (uppercase, wide letter-spacing). `Rajdhani` for all body text.
- **Depth through layers** — background grid → surface panels → glowing cards → floating elements.
- **Motion** — subtle pulse animations on neon borders, infinite rotation on ring loaders, staggered fade-ins on list items.

---

## 2. Color Palette

Define in `ui/theme/Color.kt`. Every color must be referenced by name — never use `Color(0xFF...)` inline in a Composable.

```kotlin
// ui/theme/Color.kt

// ── Backgrounds ───────────────────────────────────────────
val BgPrimary       = Color(0xFF0F172A)   // Deep navy — main screen bg
val BgSecondary     = Color(0xFF1E293B)   // Dark slate — card/panel bg
val BgDeepIndigo    = Color(0xFF1E1B4B)   // Auth screen gradient top
val BgMid           = Color(0xFF312E81)   // Auth screen gradient mid

// ── Glassmorphism Surfaces ────────────────────────────────
val GlassSurface    = Color(0x0DFFFFFF)   // rgba(255,255,255,0.05)  — glass card bg
val GlassBorder     = Color(0x1AFFFFFF)   // rgba(255,255,255,0.10)  — glass card border
val PanelBg         = Color(0xD91E293B)   // rgba(30,41,59,0.85)     — panel/sidebar bg

// ── Neon Accents ──────────────────────────────────────────
val NeonPrimary     = Color(0xFF6366F1)   // Indigo  — primary buttons, active states
val NeonSecondary   = Color(0xFF8B5CF6)   // Purple  — secondary, gradients
val NeonCyan        = Color(0xFF06B6D4)   // Cyan    — ring decoration, sidebar accent
val NeonGreen       = Color(0xFF22C55E)   // Green   — success, prices, online status
val NeonWarning     = Color(0xFFF59E0B)   // Amber   — warnings
val NeonDanger      = Color(0xFFEF4444)   // Red     — errors, destructive
val NeonInfo        = Color(0xFF3B82F6)   // Blue    — info badges
val NeonPink        = Color(0xFFEC4899)   // Pink    — sidebar count badges, highlights

// ── Neon Borders ─────────────────────────────────────────
val BorderNeon      = Color(0x336366F1)   // rgba(99,102,241, 0.20) — default border
val BorderNeonHover = Color(0x806366F1)   // rgba(99,102,241, 0.50) — hover border

// ── Text ──────────────────────────────────────────────────
val TextPrimary     = Color(0xFFF8FAFC)   // Near-white — headings, values
val TextSecondary   = Color(0xFF94A3B8)   // Cool gray  — subtitles, labels
val TextMuted       = Color(0xFF64748B)   // Dark gray  — placeholders, captions
```

**Gradient Brushes** — define in `ui/theme/Gradients.kt`:

```kotlin
// ui/theme/Gradients.kt
//import androidx.compose.ui.graphics.Brush

val GradientPrimary = Brush.linearGradient(
    colors = listOf(NeonPrimary, NeonSecondary),
    start = Offset(0f, 0f), end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
)

val GradientAuthBg = Brush.verticalGradient(
    colors = listOf(BgDeepIndigo, BgPrimary, Color(0xFF16213E))
)

val GradientGold = Brush.linearGradient(   // rank #1 badge
    colors = listOf(Color(0xFFFBBF24), Color(0xFFF59E0B))
)
val GradientSilver = Brush.linearGradient( // rank #2 badge
    colors = listOf(Color(0xFFD1D5DB), Color(0xFF9CA3AF))
)
val GradientBronze = Brush.linearGradient( // rank #3 badge
    colors = listOf(Color(0xFFF97316), Color(0xFFEA580C))
)

val GradientSuccess = Brush.linearGradient(listOf(NeonGreen, Color(0xFF16A34A)))
val GradientDanger  = Brush.linearGradient(listOf(NeonDanger, Color(0xFFDC2626)))
```

---

## 3. Typography

Fonts: **Orbitron** (headings) + **Rajdhani** (body). Bundle both in `res/font/` or use `GoogleFont` provider.

```kotlin
// ui/theme/Type.kt
val OrbitronFamily = FontFamily(
    Font(R.font.orbitron_regular, FontWeight.Normal),
    Font(R.font.orbitron_bold,    FontWeight.Bold),
    Font(R.font.orbitron_black,   FontWeight.Black),
)

val RajdhaniFamily = FontFamily(
    Font(R.font.rajdhani_light,    FontWeight.Light),
    Font(R.font.rajdhani_medium,   FontWeight.Medium),
    Font(R.font.rajdhani_semibold, FontWeight.SemiBold),
    Font(R.font.rajdhani_bold,     FontWeight.Bold),
)

val CyberTypography = Typography(
    // Screen / Page Titles — Orbitron, ALL CAPS in UI
    displayLarge  = TextStyle(fontFamily = OrbitronFamily, fontWeight = FontWeight.Black,
                               fontSize = 28.sp, letterSpacing = 3.sp, color = TextPrimary),
    displayMedium = TextStyle(fontFamily = OrbitronFamily, fontWeight = FontWeight.Bold,
                               fontSize = 22.sp, letterSpacing = 2.sp, color = TextPrimary),
    // Section headers
    headlineLarge = TextStyle(fontFamily = OrbitronFamily, fontWeight = FontWeight.Bold,
                               fontSize = 18.sp, letterSpacing = 1.5.sp, color = TextPrimary),
    headlineMedium= TextStyle(fontFamily = OrbitronFamily, fontWeight = FontWeight.Bold,
                               fontSize = 15.sp, letterSpacing = 1.sp, color = TextPrimary),
    // Card titles / nav items
    titleLarge    = TextStyle(fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold,
                               fontSize = 18.sp, letterSpacing = 1.sp, color = TextPrimary),
    titleMedium   = TextStyle(fontFamily = RajdhaniFamily, fontWeight = FontWeight.SemiBold,
                               fontSize = 15.sp, letterSpacing = 0.5.sp, color = TextSecondary),
    // Body
    bodyLarge     = TextStyle(fontFamily = RajdhaniFamily, fontWeight = FontWeight.Medium,
                               fontSize = 16.sp, color = TextPrimary),
    bodyMedium    = TextStyle(fontFamily = RajdhaniFamily, fontWeight = FontWeight.Medium,
                               fontSize = 14.sp, color = TextPrimary),
    // Labels, badges, buttons
    labelLarge    = TextStyle(fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold,
                               fontSize = 14.sp, letterSpacing = 1.sp, color = TextPrimary),
    labelSmall    = TextStyle(fontFamily = RajdhaniFamily, fontWeight = FontWeight.Medium,
                               fontSize = 11.sp, letterSpacing = 0.5.sp, color = TextMuted),
)
```

**Typography rules:**
- All page/screen titles: `.uppercase()` + `OrbitronFamily`
- All navigation labels: uppercase + `letterSpacing = 1.sp`
- Stat values (prices, counts): `OrbitronFamily` + `NeonGreen` or `NeonCyan` color
- Button text: uppercase + `RajdhaniFamily` Bold + `letterSpacing = 1.sp`

---

## 4. Spacing

```kotlin
// ui/theme/Spacing.kt
object Spacing {
    val xs   = 4.dp
    val sm   = 8.dp
    val md   = 16.dp
    val lg   = 24.dp
    val xl   = 32.dp
    val xxl  = 48.dp
}
```

Screen padding: `Modifier.padding(Spacing.lg)` (24.dp) — matches the `padding: 24px` in source CSS.

---

## 5. Shape System

```kotlin
// ui/theme/Shape.kt
val CyberShapes = Shapes(
    small  = RoundedCornerShape(6.dp),    // Badges, chips, small inputs
    medium = RoundedCornerShape(10.dp),   // Buttons, search bars, dropdowns
    large  = RoundedCornerShape(16.dp),   // Cards, bottom sheets
    extraLarge = RoundedCornerShape(20.dp) // Auth cards, modals
)

// Cyberpunk octagon clip path (for hero cards, stat boxes)
val OctagonShape = GenericShape { size, _ ->
    val cut = 10.dp.toPx()  // matches --clip-corner in CSS
    moveTo(cut, 0f)
    lineTo(size.width - cut, 0f)
    lineTo(size.width, cut)
    lineTo(size.width, size.height - cut)
    lineTo(size.width - cut, size.height)
    lineTo(cut, size.height)
    lineTo(0f, size.height - cut)
    lineTo(0f, cut)
    close()
}

val OctagonShapeSm = GenericShape { size, _ ->
    val cut = 6.dp.toPx()   // matches --clip-corner-sm
    moveTo(cut, 0f)
    lineTo(size.width - cut, 0f)
    lineTo(size.width, cut)
    lineTo(size.width, size.height - cut)
    lineTo(size.width - cut, size.height)
    lineTo(cut, size.height)
    lineTo(0f, size.height - cut)
    lineTo(0f, cut)
    close()
}
```

---

## 6. Custom Modifiers

Define in `ui/theme/Modifiers.kt`:

```kotlin
// ui/theme/Modifiers.kt

// Neon glow — replaces CSS box-shadow: 0 0 20px rgba(99,102,241,0.4)
fun Modifier.neonGlow(color: Color = NeonPrimary, radius: Dp = 20.dp) = this.drawBehind {
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(color.copy(alpha = 0.4f), Color.Transparent),
            radius = radius.toPx() * 2
        ),
        blendMode = BlendMode.Screen
    )
}

// Glassmorphism surface — replaces CSS backdrop-filter + rgba bg
fun Modifier.glassSurface() = this
    .background(GlassSurface, shape = RoundedCornerShape(20.dp))
    .border(1.dp, GlassBorder, shape = RoundedCornerShape(20.dp))

// Neon border — pulsing indigo border
fun Modifier.neonBorder(shape: Shape = RoundedCornerShape(10.dp), color: Color = BorderNeon) =
    this.border(1.dp, color, shape)

// Gradient background button
fun Modifier.gradientBackground(brush: Brush = GradientPrimary, shape: Shape = RoundedCornerShape(10.dp)) =
    this.background(brush, shape)

// Animated background grid (use on screen root Box)
// Draw hexagon grid using Canvas in a dedicated BackgroundGrid composable
```

---

## 7. Component Patterns

### 7.1 Animated Background (All Screens)

```kotlin
// ui/components/CyberBackground.kt
@Composable
fun CyberBackground(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GradientAuthBg)  // or BgPrimary for dashboard screens
    ) {
        // Animated hex grid overlay drawn on Canvas
        val infiniteTransition = rememberInfiniteTransition(label = "grid")
        val offset by infiniteTransition.animateFloat(
            initialValue = 0f, targetValue = 60f,
            animationSpec = infiniteRepeatable(tween(25000, easing = LinearEasing)),
            label = "gridOffset"
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw hex grid pattern shifted by `offset`
            // opacity 0.10, stroke NeonPrimary, strokeWidth 0.5dp
        }
        content()
    }
}
```

### 7.2 Glass Card (Auth screens)

```kotlin
// ui/components/GlassCard.kt
@Composable
fun GlassCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .glassSurface()
            .padding(Spacing.xl)  // matches padding: 2.5rem
    ) { content() }
}
```

### 7.3 Gradient Primary Button

```kotlin
// ui/components/CyberButton.kt
@Composable
fun CyberButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    icon: ImageVector? = null,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (enabled) GradientPrimary else Brush.linearGradient(
                listOf(NeonPrimary.copy(alpha = 0.5f), NeonSecondary.copy(alpha = 0.5f))))
            .neonGlow(NeonPrimary, 12.dp)
            .clickable(enabled = enabled && !isLoading, onClick = onClick)
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = TextPrimary, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
        } else {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                icon?.let { Icon(it, contentDescription = null, tint = TextPrimary) }
                Text(text.uppercase(), style = MaterialTheme.typography.labelLarge, color = TextPrimary)
            }
        }
    }
}

// Outline secondary button
@Composable
fun CyberOutlineButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, BorderNeon, RoundedCornerShape(10.dp))
            .background(Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.lg)
    ) {
        Text(text.uppercase(), style = MaterialTheme.typography.labelLarge, color = TextPrimary)
    }
}
```

### 7.4 Cyber Text Field

```kotlin
// ui/components/CyberTextField.kt
@Composable
fun CyberTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    val isFocused = remember { mutableStateOf(false) }
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(
                if (isFocused.value) NeonSecondary.copy(alpha = 0.10f) else GlassSurface,
                RoundedCornerShape(10.dp)
            )
            .border(
                1.dp,
                if (isFocused.value) NeonSecondary else BorderNeon,
                RoundedCornerShape(10.dp)
            )
            .onFocusChanged { isFocused.value = it.isFocused }
            .padding(horizontal = Spacing.md),
        decorationBox = { inner ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(leadingIcon, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(Spacing.sm))
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty()) Text(placeholder, color = TextMuted,
                        style = MaterialTheme.typography.bodyMedium)
                    inner()
                }
            }
        }
    )
}
```

### 7.5 OTP Input Row

```kotlin
// ui/components/OtpInput.kt
@Composable
fun OtpInputRow(otp: List<String>, onOtpChange: (Int, String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        otp.forEachIndexed { index, digit ->
            BasicTextField(
                value = digit,
                onValueChange = { if (it.length <= 1) onOtpChange(index, it) },
                singleLine = true,
                textStyle = TextStyle(
                    fontFamily = OrbitronFamily, fontWeight = FontWeight.Bold,
                    fontSize = 22.sp, color = TextPrimary, textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .size(width = 48.dp, height = 56.dp)
                    .background(GlassSurface, RoundedCornerShape(12.dp))
                    .border(2.dp, if (digit.isNotEmpty()) NeonSecondary else BorderNeon,
                            RoundedCornerShape(12.dp)),
                decorationBox = { inner ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { inner() }
                }
            )
        }
    }
}
```

### 7.6 Step Progress Indicator

```kotlin
// ui/components/CyberProgressSteps.kt
@Composable
fun CyberProgressSteps(currentStep: Int, labels: List<String>) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
        labels.forEachIndexed { index, label ->
            val step = index + 1
            val isActive    = step == currentStep
            val isCompleted = step < currentStep
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(35.dp)
                        .background(
                            when {
                                isCompleted -> NeonGreen
                                isActive    -> GradientPrimary
                                else        -> GlassSurface
                            }.let { if (it is Color) Brush.linearGradient(listOf(it, it)) else it as Brush },
                            shape = CircleShape
                        )
                        .border(2.dp, if (isActive) NeonSecondary else BorderNeon, CircleShape)
                ) {
                    Text("$step", style = MaterialTheme.typography.labelLarge,
                         color = if (isActive || isCompleted) TextPrimary else TextMuted)
                }
                Text(label.uppercase(), style = MaterialTheme.typography.labelSmall,
                     color = if (isActive) NeonSecondary else TextMuted)
            }
            if (index < labels.lastIndex) {
                Box(modifier = Modifier.size(width = 40.dp, height = 2.dp)
                    .padding(bottom = 16.dp)
                    .background(if (isCompleted) NeonGreen else BorderNeon))
            }
        }
    }
}
```

### 7.7 Dashboard Stat Card

```kotlin
// ui/components/StatCard.kt
@Composable
fun StatCard(label: String, value: String, change: String, isUp: Boolean,
             icon: ImageVector, accentColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgSecondary, RoundedCornerShape(16.dp))
            .border(1.dp, BorderNeon, RoundedCornerShape(16.dp))
            .drawBehind {
                // 3px top accent bar
                drawRect(
                    brush = Brush.horizontalGradient(listOf(accentColor, accentColor.copy(alpha = 0.3f))),
                    size = size.copy(height = 3.dp.toPx())
                )
            }
            .padding(Spacing.lg)
    ) {
        Column {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Box(
                    modifier = Modifier.size(52.dp)
                        .background(accentColor.copy(alpha = 0.10f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) { Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(24.dp)) }
                Surface(
                    color = if (isUp) NeonGreen.copy(alpha = 0.10f) else NeonDanger.copy(alpha = 0.10f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(change, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                         color = if (isUp) NeonGreen else NeonDanger,
                         style = MaterialTheme.typography.labelLarge)
                }
            }
            Spacer(Modifier.height(Spacing.md))
            Text(value, style = MaterialTheme.typography.displayMedium)
            Text(label, style = MaterialTheme.typography.titleMedium)
        }
    }
}
```

### 7.8 Neon Status Badge

```kotlin
// ui/components/StatusBadge.kt
@Composable
fun StatusBadge(status: String) {
    val (bg, fg) = when (status.lowercase()) {
        "completed"  -> NeonGreen.copy(alpha = 0.10f)  to NeonGreen
        "pending"    -> NeonWarning.copy(alpha = 0.10f) to NeonWarning
        "processing" -> NeonInfo.copy(alpha = 0.10f)   to NeonInfo
        "cancelled"  -> NeonDanger.copy(alpha = 0.10f) to NeonDanger
        "low stock"  -> NeonWarning.copy(alpha = 0.10f) to NeonWarning
        "online"     -> NeonGreen.copy(alpha = 0.10f)  to NeonGreen
        "critical"   -> NeonDanger.copy(alpha = 0.10f) to NeonDanger
        else         -> TextMuted.copy(alpha = 0.15f)  to TextMuted
    }
    Surface(color = bg, shape = RoundedCornerShape(20.dp)) {
        Text(status.uppercase(),
             modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
             style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp),
             color = fg)
    }
}
```

### 7.9 Sidebar Navigation Item

```kotlin
// ui/components/NavItem.kt
@Composable
fun CyberNavItem(label: String, icon: ImageVector, isActive: Boolean,
                 badge: Int? = null, onClick: () -> Unit) {
    val bgColor    = if (isActive) NeonPrimary.copy(alpha = 0.15f) else Color.Transparent
    val textColor  = if (isActive) NeonCyan else TextSecondary
    val borderColor= if (isActive) NeonCyan else Color.Transparent

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(8.dp))
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = 12.dp)
    ) {
        Icon(icon, contentDescription = null, tint = textColor, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(Spacing.md))
        Text(label.uppercase(), style = MaterialTheme.typography.labelLarge,
             color = textColor, modifier = Modifier.weight(1f))
        badge?.let {
            Surface(color = NeonPink, shape = CircleShape) {
                Text("$it", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                     style = MaterialTheme.typography.labelSmall, color = Color.White)
            }
        }
    }
}
```

### 7.10 Glowing Ring Loader (Login screen)

```kotlin
// ui/components/CyberRing.kt
@Composable
fun CyberRing(modifier: Modifier = Modifier) {
    val rings = listOf(NeonPrimary, NeonSecondary, NeonCyan)
    val infiniteTransition = rememberInfiniteTransition(label = "ring")
    val rotations = rings.mapIndexed { i, _ ->
        infiniteTransition.animateFloat(
            initialValue = 0f, targetValue = 360f,
            animationSpec = infiniteRepeatable(
                tween(durationMillis = 4000 + i * 1000, easing = LinearEasing),
                if (i % 2 == 0) RepeatMode.Restart else RepeatMode.Reverse
            ),
            label = "ring$i"
        )
    }
    Canvas(modifier = modifier.size(300.dp)) {
        rings.forEachIndexed { i, color ->
            val radius = (120.dp - i * 20.dp).toPx()
            rotate(rotations[i].value) {
                drawArc(
                    color = color.copy(alpha = 0.8f),
                    startAngle = -90f, sweepAngle = 270f, useCenter = false,
                    style = Stroke(width = (3 - i * 0.5f).dp.toPx(), cap = StrokeCap.Round),
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                )
            }
        }
    }
}
```

---

## 8. Screen Templates

### 8.1 Auth Screen (Login / Register / ForgotPassword)

```
CyberBackground (animated grid + gradient)
  └─ Box(fillMaxSize, contentAlignment = Center)
       └─ GlassCard(maxWidth = 420.dp)
            ├─ AdminLogoIcon (gradient circle, 70.dp)
            ├─ Text(title, displayLarge, uppercase)
            ├─ Text(subtitle, titleMedium)
            ├─ CyberProgressSteps (if forgot password flow)
            ├─ ErrorBanner / SuccessBanner
            ├─ CyberTextField(s)
            ├─ CyberButton("Submit")
            └─ TextButton("Back to Login")
```

### 8.2 Dashboard Screen

```
Scaffold(
  topBar = CyberTopBar,
  drawerContent = CyberSidebar
)
  └─ Column(BgPrimary, padding = Spacing.lg)
       ├─ Header (title + action buttons row)
       ├─ LazyVerticalGrid(4 cols) { StatCard x4 }
       ├─ Row { SalesChartCard, TopProductsCard }
       └─ OrdersTableCard (search + filter + table + pagination)
```

### 8.3 Products / Customers Screen

```
Column(BgPrimary)
  ├─ PageHeader (icon + title + action buttons)
  ├─ Row { 4 x StatsCountCard (octagon shape) }
  ├─ SearchAndFilterBar
  └─ LazyColumn { ProductRow / CustomerRow }
```

---

## 9. File Structure

```
app/src/main/java/com/yourapp/
├── ui/
│   ├── theme/
│   │   ├── Color.kt          ← All color tokens
│   │   ├── Gradients.kt      ← Brush gradient definitions
│   │   ├── Type.kt           ← CyberTypography (Orbitron + Rajdhani)
│   │   ├── Shape.kt          ← CyberShapes + OctagonShape
│   │   ├── Spacing.kt        ← Spacing object
│   │   ├── Modifiers.kt      ← neonGlow, glassSurface, gradientBackground
│   │   └── Theme.kt          ← CyberTheme composable (dark only)
│   ├── components/
│   │   ├── CyberBackground.kt
│   │   ├── GlassCard.kt
│   │   ├── CyberButton.kt
│   │   ├── CyberTextField.kt
│   │   ├── OtpInput.kt
│   │   ├── CyberProgressSteps.kt
│   │   ├── StatCard.kt
│   │   ├── StatusBadge.kt
│   │   ├── CyberNavItem.kt
│   │   ├── CyberRing.kt
│   │   └── NeonTopBar.kt
│   └── screens/
│       ├── auth/
│       │   ├── LoginScreen.kt
│       │   ├── RegisterScreen.kt
│       │   └── ForgotPasswordScreen.kt
│       └── dashboard/
│           ├── DashboardScreen.kt
│           ├── ProductsScreen.kt
│           └── CustomersScreen.kt
```

---

## 10. Design Rules — Anti-Patterns to Avoid

- **NEVER use a light theme** — this system is dark-only. No `lightColorScheme()`.
- **NEVER use plain `Surface` or `Card`** — always use `GlassCard` or a manually styled `Box` with `BgSecondary` + `BorderNeon`.
- **NEVER hardcode colors inline** — every color must come from `Color.kt`.
- **NEVER use `MaterialTheme.colorScheme.background`** — use `BgPrimary` directly.
- **NEVER use rounded-only buttons with no gradient** — primary actions always use `GradientPrimary`.
- **NEVER use lowercase for headings** — all `displayLarge`/`headlineLarge` text uses `.uppercase()`.
- **NEVER use default Material3 color scheme** — override everything in `CyberTheme`.
- **ALWAYS animate** — no static screens. At minimum, fade-in items with `AnimatedVisibility`.
- **ALWAYS use neon glow** on active/focused interactive elements via `neonGlow()` modifier.
- **ALWAYS use `OctagonShape`** for hero stat boxes and admin identity chips.
