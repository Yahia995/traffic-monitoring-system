from utils.config import PIXEL_TO_METER

def calculate_speed(first_frame,last_frame,positions,fps):
    if len(positions) < 2: 
        return 0.0
    
    pixel_distance = ((positions[0][0]-positions[-1][0])**2 + (positions[0][1]-positions[-1][1])**2) ** 0.5 # Euclidean distance in pixels
    meter_distance = pixel_distance * PIXEL_TO_METER # Convert pixels to meters
    
    time_per_second = (last_frame - first_frame) / fps # Time in seconds
    speed_mps = meter_distance / time_per_second # Speed in meters per second

    speed_kph = speed_mps * 3.6 # Convert to kilometers per hour

    return round(speed_kph, 2)