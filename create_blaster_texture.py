#!/usr/bin/env python3
"""
Creates a simple gun texture for the blaster item.
Generates a 16x16 pixel image with a box and handle design.
"""

try:
    from PIL import Image, ImageDraw
except ImportError:
    print("Pillow is not installed. Installing...")
    import subprocess
    import sys
    subprocess.check_call([sys.executable, "-m", "pip", "install", "Pillow", "--quiet"])
    from PIL import Image, ImageDraw

# Create 16x16 image with transparent background
img = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
draw = ImageDraw.Draw(img)

# Colors - dark gray gun with highlights
dark_gray = (45, 45, 45, 255)      # Main body
medium_gray = (80, 80, 80, 255)    # Barrel
light_gray = (120, 120, 120, 255)  # Highlights
black = (20, 20, 20, 255)          # Outlines
handle_dark = (30, 30, 30, 255)     # Handle

# Draw main body (box) - center-left area
draw.rectangle([2, 5, 8, 11], fill=dark_gray, outline=black)
# Add highlight on left side
draw.rectangle([3, 6, 4, 10], fill=light_gray)

# Draw barrel - extending to the right (longer rectangle)
draw.rectangle([8, 6, 14, 10], fill=medium_gray, outline=black)
# Barrel tip
draw.rectangle([14, 7, 15, 9], fill=dark_gray, outline=black)
# Barrel highlight
draw.line([9, 7, 13, 7], fill=light_gray, width=1)

# Draw handle/grip - extending downward from left side
draw.rectangle([2, 11, 5, 15], fill=handle_dark, outline=black)
# Handle grip lines
draw.line([3, 12, 3, 14], fill=dark_gray, width=1)
draw.line([4, 12, 4, 14], fill=dark_gray, width=1)

# Add some detail lines on the body
draw.line([5, 7, 7, 7], fill=medium_gray, width=1)  # Top detail
draw.line([5, 9, 7, 9], fill=medium_gray, width=1)  # Bottom detail

# Save to the correct location
output_path = r'src\main\resources\assets\morestuffintheend\textures\item\blaster.png'
img.save(output_path)
print(f"✓ Blaster gun texture created at: {output_path}")
print("  The texture shows a simple gun with a box body, barrel, and handle.")

