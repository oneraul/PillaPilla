varying vec4 v_color;
varying vec2 v_texCoord0;

uniform sampler2D u_texture;
uniform sampler2D u_otherTexture;
uniform sampler2D u_mask;
uniform float u_threshold;
uniform int u_mode;

void main() {
	vec4 colorA = texture2D(u_texture, v_texCoord0);
	vec4 colorB = texture2D(u_otherTexture, v_texCoord0);
	float mask = texture2D(u_mask, v_texCoord0).r;

    if(u_mode > 0.5) {
        vec4 finalColor;
        float f = smoothstep(u_threshold-0.05, u_threshold+0.05, mask);
        finalColor = (1-f)*colorA + f*colorB;
        gl_FragColor = v_color * finalColor;

	} else {
        gl_FragColor = v_color * mix(colorA, colorB, mask);
    }
}
