from utils.config import MAX_DISAPPEARED, MAX_DISTANCE

class CentroidTracker:
    def __init__(self, max_disappeared=MAX_DISAPPEARED, max_distance=MAX_DISTANCE):
        self.next_object_id = 0
        self.objects = {}        # object ID -> centroid
        self.disappeared = {}    # object ID -> disappeared count
        self.max_disappeared = max_disappeared
        self.max_distance = max_distance

    def register(self, centroid):
        oid = self.next_object_id
        self.objects[oid] = centroid
        self.disappeared[oid] = 0
        self.next_object_id += 1
        return oid

    def deregister(self, oid):
        del self.objects[oid]
        del self.disappeared[oid]

    def distance(self, c1, c2):
        return ((c1[0] - c2[0]) ** 2 + (c1[1] - c2[1]) ** 2) ** 0.5

    def update(self, rects):

        # CASE 1 — No detections this frame
        if len(rects) == 0:
            to_remove = []
            for oid in list(self.disappeared.keys()):
                self.disappeared[oid] += 1
                if self.disappeared[oid] > self.max_disappeared:
                    to_remove.append(oid)

            for oid in to_remove:
                self.deregister(oid)

            return {}

        # Compute centroids from rects
        centroids = []
        for x1, y1, x2, y2 in rects:
            cX, cY = (x1 + x2) // 2, (y1 + y2) // 2
            centroids.append((cX, cY))

        updated = {}

        # CASE 2 — No tracked objects yet
        if len(self.objects) == 0:
            for i, centroid in enumerate(centroids):
                oid = self.register(centroid)
                updated[oid] = rects[i]
            return updated

        # CASE 3 — Match old objects with new detections
        new_centroids = centroids
        old_centroids = list(self.objects.values())
        object_ids = list(self.objects.keys())
        pairs = []

        for i, c_new in enumerate(new_centroids):
            for j, c_old in enumerate(old_centroids):
                d = self.distance(c_new, c_old)
                pairs.append((i, j, d))

        pairs.sort(key=lambda p: p[2])

        used_new = set()
        used_old = set()

        for i, j, d in pairs:
            if i in used_new or j in used_old or d > self.max_distance:
                continue

            oid = object_ids[j]
            self.objects[oid] = new_centroids[i]
            self.disappeared[oid] = 0
            updated[oid] = rects[i]

            used_new.add(i)
            used_old.add(j)

        # Objects disappeared
        for j in range(len(object_ids)):
            if j not in used_old:
                oid = object_ids[j]
                self.disappeared[oid] += 1
                if self.disappeared[oid] > self.max_disappeared:
                    self.deregister(oid)

        # New objects appeared
        for i in range(len(new_centroids)):
            if i not in used_new:
                oid = self.register(new_centroids[i])
                updated[oid] = rects[i]

        return updated
