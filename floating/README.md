# Floating Bubble View - Java Module

This is a Java-only conversion of the original Kotlin `FloatingBubbleView` module. It provides the same floating bubble functionality but implemented entirely in Java for developers who prefer Java over Kotlin.

## Features

- **Java-only implementation**: No Kotlin dependencies required
- **Android API 21+ support**: Compatible with Android 5.0 and above
- **Same API surface**: Maintains compatibility with existing Java usage patterns
- **Floating bubble functionality**: Draggable floating bubbles with expand/collapse capability
- **Close bubble support**: Drag-to-close functionality
- **Animation support**: Spring animations for smooth bubble movement
- **Background service**: Proper foreground service implementation

## Module Structure

```
floating/
├── src/main/java/com/torrydo/floating/
│   ├── FloatingBubbleListener.java      // Interface for bubble events
│   ├── CloseBubbleBehavior.java         // Enum for close bubble behavior
│   ├── AndroidVersions.java             // Android version constants
│   ├── Utils.java                       // General utility methods
│   ├── ViewUtils.java                   // View-related utility methods
│   ├── AnimHelper.java                  // Spring animation helpers
│   ├── XMath.java                       // Math utilities
│   ├── MyBubbleLayout.java              // Custom layout for bubbles
│   ├── bubble/
│   │   ├── Bubble.java                  // Base bubble class
│   │   ├── FloatingBubble.java          // Main floating bubble
│   │   ├── FloatingCloseBubble.java     // Close bubble implementation
│   │   └── FloatingBottomBackground.java // Background overlay
│   ├── helper/
│   │   ├── ViewHelper.java              // View creation helpers
│   │   └── NotificationHelper.java      // Notification management
│   ├── service/
│   │   ├── FloatingBubbleService.java   // Base service class
│   │   └── expandable/
│   │       ├── ExpandableBubbleService.java  // Main service implementation
│   │       ├── BubbleBuilder.java            // Builder for bubble configuration
│   │       └── ExpandedBubbleBuilder.java    // Builder for expanded bubble
│   └── example/
│       └── ExampleFloatingService.java  // Usage example
└── src/main/res/                        // Resources (layouts, drawables, styles)
```

## Key Differences from Kotlin Version

### Removed Features
- **Compose support**: All Jetpack Compose integration has been removed
- **Coroutines**: Kotlin coroutines dependencies removed
- **Extension functions**: Converted to static utility methods

### Converted Features
- **Object declarations** → Final utility classes with private constructors
- **Data classes** → Regular classes with getters/setters
- **Default parameters** → Method overloads
- **Lambda expressions** → Functional interfaces and anonymous classes
- **Extension functions** → Static utility methods in `Utils` and `ViewUtils`
- **Kotlin properties** → Java getters/setters
- **Safe navigation (`?.`)** → Explicit null checks

## Usage Example

```java
public class MyFloatingService extends ExpandableBubbleService {

    @Nullable
    @Override
    public BubbleBuilder configBubble() {
        View bubbleView = ViewHelper.fromDrawable(this, R.drawable.my_bubble_icon, 60, 60);
        
        return new BubbleBuilder(this)
                .bubbleView(bubbleView)
                .bubbleDraggable(true)
                .forceDragging(false)
                .closeBubbleView(ViewHelper.fromDrawable(this, R.drawable.close_icon))
                .distanceToClose(100)
                .closeBehavior(CloseBubbleBehavior.FIXED_CLOSE_BUBBLE)
                .startLocation(100, 100)
                .enableAnimateToEdge(true)
                .addFloatingBubbleListener(new FloatingBubbleListener() {
                    @Override
                    public void onFingerDown(float x, float y) {
                        // Handle touch down
                    }
                    
                    @Override
                    public void onFingerUp(float x, float y) {
                        // Handle touch up
                    }
                    
                    @Override
                    public void onFingerMove(float x, float y) {
                        // Handle touch move
                    }
                });
    }

    @Nullable
    @Override
    public ExpandedBubbleBuilder configExpandedBubble() {
        View expandedView = LayoutInflater.from(this).inflate(R.layout.expanded_bubble, null);
        
        return new ExpandedBubbleBuilder(this)
                .expandedView(expandedView)
                .draggable(true);
    }
}
```

## Dependencies

The module uses only Java-compatible Android dependencies:

```gradle
dependencies {
    implementation 'androidx.core:core:1.10.1'
    implementation 'androidx.dynamicanimation:dynamicanimation:1.0.0'
    implementation 'io.github.torrydo:screen-easy:0.1.0'
}
```

## Permissions

Add these permissions to your app's `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
<uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>
```

## Migration from Kotlin Module

If you're migrating from the original Kotlin module, the API is largely the same for Java usage. The main changes are:

1. Package name: `com.torrydo.floating` instead of `com.torrydo.floatingbubbleview`
2. No Compose support: Remove any Compose-related code
3. Extension functions: Use static methods in `Utils` and `ViewUtils` classes
4. Functional interfaces: Use anonymous classes instead of lambda expressions if targeting older Java versions

## Target Requirements

- **Minimum SDK**: API 21 (Android 5.0)
- **Target SDK**: API 34
- **Java Version**: 1.8+
- **Build Tools**: Android Gradle Plugin 8.0+