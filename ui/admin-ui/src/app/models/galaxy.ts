import { GalaxyStatus } from "./galaxy-status";

export interface Galaxy {
    id: number;
    name: string;
    address: string;
    port: number;
    timeZone: number;
    created: number;
    onlineCount: number;
    status: GalaxyStatus;
}