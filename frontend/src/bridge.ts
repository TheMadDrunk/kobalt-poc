import { createSignal, onCleanup } from "solid-js";

interface KotlinBridge {
  sendMessage(jsonString: string): string;
}

declare global {
  interface Window {
    app?: KotlinBridge;
    receiveMessage?: (jsonString: string) => void;
  }
}

export const useBridge = () => {
  const [lastMessage, setLastMessage] = createSignal<string | null>(null);
  const [pings, setPings] = createSignal(0);

  const receiveMessage = (jsonString: string) => {
    console.log("JS RECEIVED MESSAGE:", jsonString);
    setLastMessage(jsonString);
    setPings((p) => p + 1);
  };

  // Register the global function that Kotlin calls
  window.receiveMessage = receiveMessage;

  onCleanup(() => {
    delete window.receiveMessage;
  });

  const sendMessage = (message: string) => {
    if (window.app) {
      window.app.sendMessage(message);
    } else {
      console.warn("Kotlin bridge not available");
    }
  };

  return { sendMessage, lastMessage, pings };
};
