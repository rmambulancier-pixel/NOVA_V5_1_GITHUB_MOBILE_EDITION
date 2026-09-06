# NOVA / Alphonse Logo Assets

This folder contains three polished vector variants for the NOVA application logo, designed for a premium dark AI look:

1) ic_alphonse_mark.xml — primary mark: friendly robot head with subtle glow and chest badge. Good for launcher foreground and app icon.
2) ic_alphonse_emblem.xml — compact circular emblem with a stylized "N" monogram. Good for avatars and small placements.
3) ic_nova_wordmark.xml — wordmark composed of stylized letter blocks to match the brand look.

Adaptive icons (mipmap-anydpi):
- ic_launcher_alphonse_v2.xml (foreground = ic_alphonse_mark)
- ic_launcher_alphonse_emblem.xml (foreground = ic_alphonse_emblem)
- ic_launcher_nova_wordmark.xml (foreground = ic_nova_wordmark)

Background drawable ic_launcher_bg_deep.xml provides a subtle deep blue gradient for adaptive icon backgrounds.

How to preview/use:
- To use variant 2 as the launcher icon, update AndroidManifest.xml application attributes:
  android:icon="@mipmap/ic_launcher_alphonse_v2"
  android:roundIcon="@mipmap/ic_launcher_alphonse_v2"

- For Play Store, export a 512x512 PNG from the vector with Android Studio Asset Studio.

If you want, I can:
- set variant 2 as the default launcher icon in the manifest (and push the change),
- generate high-quality PNG raster assets (mdpi..xxxhdpi + 512) from these vectors and push them,
- create a quick preview PNG showing each variant on a simulated launcher background.

Tell me what you'd like me to do next: set variant 2 as default, auto-generate PNGs, or create previews.
