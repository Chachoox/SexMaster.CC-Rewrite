/*
 * Original shader from: https://www.shadertoy.com/view/ttccRS
 */

#ifdef GL_ES
precision mediump float;
#endif

// glslsandbox uniforms
uniform float time;
uniform vec2 resolution;

// shadertoy emulation
#define iTime time
#define iResolution resolution

// --------[ Original ShaderToy begins here ]---------- //
// This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 3.0
// Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/3.0/
// or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
// =========================================================================================================

mat2 r2d(float a){float cosa = cos(a);float sina = sin(a);return mat2(cosa,sina,-sina,cosa);}

float sat(float a)
{
    return clamp(a,0.,1.);
}
float lenny(vec2 v)
{
    return abs(v.x)+abs(v.y);
}

float loz(vec2 p,float r)
{
    return lenny(p)-r;
}

float sub(float a, float b)
{
    return max(a,-b);
}
float star(vec2 uv,float r)
{
    return mix(lenny(uv),length(uv),-2.)-r;
}


vec3 rdr(vec2 uv)
{
    vec3 col;
    float th = .005;
    float lz = abs(loz(uv*vec2(1.5,1.),.5))-th;
    col += vec3(.7,0.5,0.3)*pow(1.-sat(lz*2.),3.);
    col += vec3(1.,0.52,0.7)*(1.-sat(lz*200.));
    col += vec3(.7,0.52,0.56)*(1.-sat((lenny(uv*vec2(.8,2.))-.3)*1.));
    return col;
}

float ti;
void mainImage( out vec4 fragColor, in vec2 fragCoord ) {
    ti = iTime*0.3;
    vec2 uv = (fragCoord.xy-.5*iResolution.xy) / iResolution.xx;
    // uv*=2.2*(sin(iTime)*.2+.5);
    // uv*= 1.+(sin(iTime)*.5+.5)*.1*(mod(iTime,.1)/.1)*(abs(uv.x+sin(iTime))+.5);
    vec2 ouv = uv;
    uv*= r2d(-ti);
    uv*= r2d(float(int(ti+uv.y*10.*uv.x)));
    uv*=1.;
    uv= (mod(uv*(sin(ti)*.3+1.)*5.,vec2(2.))-vec2(1.))*r2d(ti);

    vec3 col = rdr(.7*uv*r2d(5.*lenny(uv)-ti*3.))
    +rdr(uv)
    +rdr(-uv*5.);
    col *= .5+vec3(uv, .25+.5*sin(ti));
    col = mix(col,col.zxy,float(mod(lenny(ouv)-ti,.5)<.2));
    col *= 1.-sat((lenny(ouv)-.5)*2.);
    if (ouv.x<0.)
    col = col.yxz;
    col += .5*vec3(ouv*.5,sin(ti)*.2+.8)*pow(1.-sat((lenny(ouv)-.5)*1.),2.);
    fragColor = vec4(col, 1.0);
}

// --------[ Original ShaderToy ends here ]---------- //

void main(void)
{
    mainImage(gl_FragColor, gl_FragCoord.xy);
}