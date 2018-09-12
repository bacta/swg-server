import { environment } from "../../environments/environment";

export class UrlHelper {
    /**
     * Concatenates an endpoint with a base address. If no base address is provided, then
     * the configured api base address is assumed. Ensures that paths are properly separated.
     * @param endpoint The endpoint to concatenate to the base path.
     * @param base     The base path to use. Defaults to <code>environment.api</code>.
     */
    static build(endpoint: string, base: string = environment.api.url): string {
        if (!endpoint.startsWith('/'))
            endpoint = '/' + endpoint;

        return `${base}${endpoint}`;
    }
}