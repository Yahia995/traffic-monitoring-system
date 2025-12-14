def safe_crop(image, bbox):
    h, w = image.shape[:2] # shape: (height, width, channels)

    x1, y1, x2, y2 = bbox

    # Ensure coordinates are within image bounds
    x1 = max(0, x1)
    y1 = max(0, y1)
    x2 = min(w, x2)
    y2 = min(h, y2)

    if x1 >= x2 or y1 >= y2:
        return None
    
    return image[y1:y2, x1:x2]