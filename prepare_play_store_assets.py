import os
import shutil
from PIL import Image, ImageDraw, ImageFont

output_dir = r"e:\stitch_absforge_fitness_app_design\play_store_assets"
os.makedirs(output_dir, exist_ok=True)

# 1. Create 512x512 App Icon
icon_size = (512, 512)
icon_img = Image.new("RGBA", icon_size, (8, 9, 9, 255))
draw = ImageDraw.Draw(icon_img)

# Outer rounded rectangle / glow
draw.rounded_rectangle([(16, 16), (496, 496)], radius=110, fill=(20, 20, 20, 255), outline=(255, 48, 48, 255), width=8)

# Inner Shield / Core Shape
shield_points = [
    (256, 75),
    (410, 140),
    (380, 340),
    (256, 440),
    (132, 340),
    (102, 140)
]
draw.polygon(shield_points, fill=(35, 12, 12, 255), outline=(255, 48, 48, 255))

# Dumbbell / Core Bars
# Center bar
draw.rounded_rectangle([(180, 240), (332, 272)], radius=12, fill=(255, 48, 48, 255))
# Left weight
draw.rounded_rectangle([(150, 195), (190, 317)], radius=16, fill=(255, 255, 255, 255))
draw.rounded_rectangle([(120, 215), (150, 297)], radius=12, fill=(255, 48, 48, 255))
# Right weight
draw.rounded_rectangle([(322, 195), (362, 317)], radius=16, fill=(255, 255, 255, 255))
draw.rounded_rectangle([(362, 215), (392, 297)], radius=12, fill=(255, 48, 48, 255))

# Top core flame / crown
flame_points = [(256, 110), (280, 165), (256, 190), (232, 165)]
draw.polygon(flame_points, fill=(255, 48, 48, 255))

icon_path = os.path.join(output_dir, "01_app_icon_512x512.png")
icon_img.convert("RGB").save(icon_path, "PNG", quality=95)
print(f"Generated Icon: {icon_path}")

# 2. Create 1024x500 Feature Graphic
fg_size = (1024, 500)
fg_img = Image.new("RGBA", fg_size, (8, 9, 9, 255))
fg_draw = ImageDraw.Draw(fg_img)

# Background gradient / accents
for y in range(500):
    alpha = int(45 * (1 - abs(y - 250) / 250.0))
    fg_draw.line([(0, y), (1024, y)], fill=(255, 48, 48, alpha))

# Red glowing border
fg_draw.rectangle([(0, 0), (1023, 499)], outline=(255, 48, 48, 180), width=4)

# Header text & Badge
fg_draw.rounded_rectangle([(80, 70), (320, 110)], radius=20, fill=(40, 12, 12, 255), outline=(255, 48, 48, 255), width=2)

# Paste scaled icon on right
scaled_icon = icon_img.resize((320, 320), Image.Resampling.LANCZOS)
fg_img.paste(scaled_icon, (630, 90), scaled_icon)

fg_path = os.path.join(output_dir, "02_feature_graphic_1024x500.png")
fg_img.convert("RGB").save(fg_path, "PNG", quality=95)
print(f"Generated Feature Graphic: {fg_path}")

# 3. Copy & format screenshots
screenshots_map = [
    ("home_dashboard", "03_screenshot_1_home_dashboard.png"),
    ("polished_active_workout", "04_screenshot_2_workout_player.png"),
    ("enhanced_rest_timer", "05_screenshot_3_rest_timer.png"),
    ("celebratory_workout_complete", "06_screenshot_4_workout_complete.png"),
    ("progress_body_tracker", "07_screenshot_5_progress_tracker.png"),
    ("exercise_library_details", "08_screenshot_6_exercise_library.png"),
    ("redesigned_30_day_program", "09_screenshot_7_30_day_program.png"),
]

for src_folder, dest_filename in screenshots_map:
    src_file = os.path.join(r"e:\stitch_absforge_fitness_app_design", src_folder, "screen.png")
    dest_file = os.path.join(output_dir, dest_filename)
    if os.path.exists(src_file):
        img = Image.open(src_file)
        img.save(dest_file, "PNG")
        print(f"Saved Screenshot: {dest_file} ({img.size})")

print("All Play Store assets prepared successfully!")
