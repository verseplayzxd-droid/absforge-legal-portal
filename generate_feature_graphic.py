import os
from PIL import Image, ImageDraw, ImageFont, ImageFilter, ImageEnhance

output_path = r"e:\stitch_absforge_fitness_app_design\play_store_assets\feature_graphic_1024x500.png"
user_img_path = r"C:\Users\Yashpc\.gemini\antigravity\brain\af9a3ce9-6084-47a0-8b72-33dd55e825fe\.user_uploaded\media_1787133083532.png" # Workout at home image with athlete & phone

# Target 1024x500 canvas
canvas = Image.new("RGBA", (1024, 500), (8, 9, 9, 255))

if os.path.exists(user_img_path):
    user_img = Image.open(user_img_path).convert("RGBA")
    
    # Extract the logo from top left: (20, 20, 300, 160)
    logo_crop = user_img.crop((20, 20, 320, 160))
    
    # Extract athlete / phone area to place on the right side
    # Resize athlete portion to fit right side (approx 450x500)
    side_crop = user_img.crop((0, 400, 576, 1024))
    side_crop = side_crop.resize((480, 500), Image.Resampling.LANCZOS)
    
    # Paste side graphic on right
    canvas.paste(side_crop, (544, 0))
    
    # Add a smooth dark gradient overlay on left to make text pop
    gradient = Image.new("RGBA", (1024, 500), (0, 0, 0, 0))
    g_draw = ImageDraw.Draw(gradient)
    for x in range(1024):
        # Alpha decreases towards the right
        if x < 450:
            alpha = 255
        elif x < 700:
            alpha = int(255 * (1 - (x - 450) / 250.0))
        else:
            alpha = 0
        g_draw.line([(x, 0), (x, 500)], fill=(8, 9, 9, alpha))
        
    canvas = Image.alpha_composite(canvas, gradient)
    
    # Paste Logo at top left
    scaled_logo = logo_crop.resize((int(logo_crop.width * 1.3), int(logo_crop.height * 1.3)), Image.Resampling.LANCZOS)
    canvas.paste(scaled_logo, (60, 45), scaled_logo)

draw = ImageDraw.Draw(canvas)

# Red accent glow lines
draw.line([(0, 498), (1024, 498)], fill=(255, 48, 48, 255), width=3)
draw.line([(0, 0), (1024, 0)], fill=(255, 48, 48, 120), width=1)

# Large Catchy Text on Left
# Try system font or clean default fallback
try:
    font_bold = ImageFont.truetype("arialbd.ttf", 46)
    font_large = ImageFont.truetype("impact.ttf", 64)
    font_sub = ImageFont.truetype("arialbd.ttf", 22)
    font_badge = ImageFont.truetype("arialbd.ttf", 18)
except:
    font_bold = font_large = font_sub = font_badge = ImageFont.load_default()

# Main Title Line
draw.text((60, 200), "30 DAY ABS WORKOUT", fill=(255, 255, 255, 255), font=font_large)
draw.text((60, 275), "SCULPT YOUR CORE AT HOME", fill=(255, 48, 48, 255), font=font_bold)

# Badges pill tags
tags = ["NO EQUIPMENT NEEDED", "ALL FITNESS LEVELS", "AUDIO COACH"]
start_x = 60
y_tag = 360

for tag in tags:
    # Pill box
    bbox = draw.textbbox((start_x + 16, y_tag + 8), tag, font=font_badge)
    w = bbox[2] - bbox[0] + 32
    draw.rounded_rectangle([(start_x, y_tag), (start_x + w, y_tag + 38)], radius=19, fill=(24, 12, 12, 240), outline=(255, 48, 48, 255), width=2)
    draw.text((start_x + 16, y_tag + 9), tag, fill=(245, 245, 247, 255), font=font_badge)
    start_x += w + 16

# Convert and save
final_img = canvas.convert("RGB")
final_img.save(output_path, "PNG", quality=95)
print(f"Successfully generated Feature Graphic: {output_path} (Size: {final_img.size})")
