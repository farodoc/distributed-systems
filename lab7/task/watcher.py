from kazoo.client import KazooClient
from kazoo.protocol.states import WatchedEvent, EventType
import kazoo.exceptions
import pygame
import threading

class Watcher:
    def __init__(self, hosts='localhost:2181'):
        self.zk = KazooClient(hosts=hosts)
        self.zk.start()
        self.display_thread = None
        self.running = False
        self.children_count = 0

        self.zk.exists("/a", watch=self.watch_node_a)
        
    def watch_node_a(self, event: WatchedEvent):
        if event.type == EventType.CREATED:
            self.start_display()
            self.zk.get_children("/a", watch=self.watch_children)
            self.watch_all_descendants("/a")
        elif event.type == EventType.DELETED:
            self.stop_display()

        self.zk.exists("/a", watch=self.watch_node_a)

    def count_descendants(self, path="/a"):
        if not self.zk.exists(path):
            return 0
            
        total = 0
        children = self.zk.get_children(path)
        total += len(children)
        
        for child in children:
            total += self.count_descendants(f"{path}/{child}")
            
        return total

    def watch_children(self, event: WatchedEvent):
        if event.type == EventType.CHILD:
            try:
                self.children_count = self.count_descendants("/a")
                self.watch_all_descendants("/a")
            except kazoo.exceptions.NoNodeError:
                self.children_count = 0

        if self.zk.exists("/a"):
            self.zk.get_children("/a", watch=self.watch_children)

    def watch_all_descendants(self, path):
        if not self.zk.exists(path):
            return
            
        children = self.zk.get_children(path, watch=self.watch_children)
        for child in children:
            child_path = f"{path}/{child}"
            self.watch_all_descendants(child_path)

    def update_display(self):
        pygame.init()
        screen = pygame.display.set_mode((400, 100))
        pygame.display.set_caption("ZooKeeper Children Counter")
        font = pygame.font.Font(None, 36)

        while self.running:
            for event in pygame.event.get():
                if event.type == pygame.QUIT:
                    self.running = False
                    
            screen.fill((255, 255, 255))
            text = font.render(f"Node 'a' has {self.children_count} children", True, (0, 0, 0))
            text_rect = text.get_rect(center=(200, 50))
            screen.blit(text, text_rect)
            pygame.display.flip()
            pygame.time.wait(100)

        pygame.quit()

    def start_display(self):
        if not self.display_thread:
            self.running = True
            self.display_thread = threading.Thread(target=self.update_display)
            self.display_thread.daemon = True
            self.display_thread.start()

    def stop_display(self):
        if self.display_thread:
            self.running = False
            self.display_thread.join()
            self.display_thread = None
            self.children_count = 0

    def print_tree(self, path="/a", level=0):
        if self.zk.exists(path):
            print("  " * level + path.split("/")[-1])
            children = self.zk.get_children(path)
            for child in children:
                self.print_tree(f"{path}/{child}", level + 1)

    def close(self):
        self.stop_display()
        self.zk.stop()
        self.zk.close()

if __name__ == "__main__":
    watcher = Watcher()
    
    try:
        while True:
            print("Available commands: 'tree', 'exit'")
            command = input("Enter command: ").strip().lower()
            if command == "tree":
                watcher.print_tree()
            elif command == "exit":
                break
    finally:
        watcher.close()
