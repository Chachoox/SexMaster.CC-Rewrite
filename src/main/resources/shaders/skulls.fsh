/*
 * Original shader from: https://www.shadertoy.com/view/3ddyz2
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

// Emulate some GLSL ES 3.x
#define round(x) (floor((x) + 0.5))

// --------[ Original ShaderToy begins here ]---------- //
#define FAR 4.


//just wanted to add that a lot of the lighting stuff is Shane's
//and his comments are still there.


float sdCappedCylinder( vec3 p, float h, float r ){
    vec2 d = abs(vec2(length(p.xz),p.y)) - vec2(h,r);
    return min(max(d.x,d.y),0.0) + length(max(d,0.0));
}

float smin( float a, float b, float k ){
    float h = clamp( 0.5 + 0.5 * (b - a) / k, 0.0, 1.0 );
    return mix( b, a, h ) - k * h * (1.0 - h);
}

float smax( float a, float b, float k ){
    float h = clamp( 0.5 + 0.5 * (a - b) / k, 0.0, 1.0 );
    return mix( b, a, h ) + k * h * (1.0 - h);
}

float knochen(vec3 p, vec3 xyz, vec3 dim, float th, float phi, float ga,

float spiegel, float r){

    vec3 sp = p - xyz;
    sp.x = abs(sp.x) - spiegel;

    float cs = cos(th), si = sin(th);
    sp.yz*=mat2(cs, si, -si, cs);

    cs = cos(phi), si = sin(phi);
    sp.xz*= mat2(cs, si, -si, cs);

    cs = cos(ga), si = sin(ga);
    sp.xy*= mat2(cs, si, -si, cs);

    sp = abs(sp) - dim;
    return min(max(sp.x, max(sp.y, sp.z)), 0.0) + length(max(sp, 0.0)) - r;
}

float sdCircleCircle( in vec3 p, in int n, float zeichen){
    float a0 = float(n) / radians(360.);
    float a = (atan(p.z, p.x) * a0) / a0;
    vec3 sp = p - vec3(cos(a), 0.0, sin(a));

    float cs = cos(a), si = sin(a);//erste versuch!!!!
    sp.xz*=mat2(cs, si, -si, cs);

    cs = cos(0.3*zeichen), si = sin(0.3*zeichen);//erste versuch!!!!
    sp.xy*=mat2(cs, si, -si, cs);

    sp = abs(sp) - vec3(0.1 + cos(sp.y*8.)*0.03, 0.19, 0.07 + cos(sp.y*6.+ 1.4)*0.05*zeichen);
    return min(max(sp.x, max(sp.y, sp.z)), 0.0) + length(max(sp, 0.0)) - 0.03;


    //return sdCappedCylinder(sp, 0.05, 0.06) - 0.05;
}

const mat3 m = mat3( 0.00,  0.80,  0.60,
-0.80,  0.36, -0.48,
-0.60, -0.48,  0.64 );
vec4 objID, oSvObjID;
float svObjID = 0.; // Global ID to keep a copy of the above from pass to pass.
float map(vec3 p){
    vec3 altp = p;
    p.x+=1.;
    p.x = mod(p.x, 2.) - 1.;

    float PosX, PosY;
    PosX = 0. + sin(iTime*20.)/4.;// - (iMouse.x)/iResolution.x;//0.2*2.*(rechts - links);
    PosY = -0.1 + sin(iTime*2.)/4.;;//(iMouse.y)/iResolution.y*4.;

    //zufall
    float cs = cos(PosX), si = sin(PosX);
    //p.xy = mat2(cs, si, -si, cs)*p.xy;
    if (altp.x > 0.75 || altp.x < -0.75){
        p.xz = mat2(cs, si, -si, cs)*p.xz;}
    else{
        cs = cos(PosY), si = sin(PosY);
        p.yz = mat2(cs, si, -si, cs)*p.yz;}

    //guckmal
    float gu = clamp(sin(iTime*0.3), -0.4, 0.4);
    cs = cos(gu), si = sin(gu);
    //p.xz = mat2(cs, si, -si, cs)*p.xz;


    p*=0.6;
    p.y-=0.2;// + sin(iTime)*0.1;



    vec3 sp = p - vec3(0., 0., 0.03);
    float k = length(sp*vec3(1.-sin(sp.z*6.-0.4)*0.05,
    0.95+sin(sp.z*6.+1.6)*0.05,
    0.87)) - 0.5;
    float alle = k;

    //schadel unten schneiden
    sp = p;
    k = length(sp - vec3(0., -0.9, -0.5)) - 0.7;
    alle = smax(alle, -k, 0.09);

    ///return alle;

    //schadel seite
    sp = p  - vec3(0., -0.05, -0.28);
    sp.x = abs(sp.x) - 0.65;
    //length(sp*vec3(1., 0.95+sin(sp.z*4.+0.8)*0.07, 0.87))
    k = length(sp) - 0.3;
    //k = length(sp*vec3(1., 0.95+sin(sp.z*4.+0.8)*0.07, 0.6)) - 0.3;
    //alle = smax(alle, -k, 0.09);

    //schadel seite knochen schneiden versuch
    sp = p;
    k = knochen(p, vec3(0., -0.15 , -0.18 ),
    vec3(0.005,
    0.17 + sin(sp.z*4.+2.4)*0.1,
    0.2+ sin(sp.y*4.+2.)*0.18),
    -0.2, -0.4, 0.1, 0.42+ sin(sp.z*4.+2.4)*0.05, 0.03);
    alle = smax(alle, -k, 0.09);

    //return alle;

    //oben augen knochen
    sp = p;
    k = knochen(p, vec3(0.,
    0. + cos(abs(sp.x)*8. + 5.)*0.03*1. - 0.04*1. ,
    -0.45 - cos(abs(sp.x)*8. + 5.8)*0.05),
    vec3(0.12, 0.005, 0.005),
    0., 0., -0., 0.14, 0.04);
    alle = smin(alle, k, 0.07);

    //return alle;


    //oberest knochen
    sp = p;
    k = knochen(p, vec3(0., 0.055 , -0.37 ), vec3(0.005, 0.005, 0.12),
    -1., -0.25, 0., 0.28, 0.02);
    alle = smin(alle, k, 0.06);

    //augen seit knochen
    sp = p;
    k = knochen(p, vec3(0., -0.14 , -0.46+ cos(sp.y*8.+1.5)*0.07),
    vec3(0.01, 0.08, 0.02),
    0.0, 0.0, 0.35, 0.26+ cos(sp.y*12.+1.2)*0.06, 0.015);
    alle = smin(alle, k, 0.06);




    //wangen knochen vor
    sp = p;
    k = knochen(p, vec3(0., -0.28, -0.42+cos(abs(sp.x)*8.+2.)*0.02),
    vec3(0.13, 0.055+sin(abs(sp.x)*8.+2.)*0.02, 0.05),
    -0.5, 0.2, -0.1, 0.12, 0.05);
    alle = smin(alle, k, 0.09);

    //return alle;

    //wangen knochen seit sollte dasselb (gesamt)breit haben wie die augen seit!
    //wangen knochen seit
    sp = p;
    k = knochen(p, vec3(0., -0.3, -0.28),
    vec3(0.007, 0.02-cos(sp.z*8. + 1.7-cos(sp.z*20. + 1.7))*0.01, 0.16),
    0., 0.1, 0., 0.27 + cos(sp.z*8. + 2.)*0.09,
    0.03 + cos(sp.z*8. + 4.7)*0.015);
    alle = smin(alle, k, 0.02);

    float keiferHohe = 0.01;
    //wangen knochen zu keifer
    sp = p;
    k = knochen(p, vec3(0., -0.51+keiferHohe, -0.25 - cos(abs(sp.y)*12. + 3.)*0.04),
    vec3(0.005-cos(sp.z*12.+4.5)*0.004 - cos(sp.y*6.+1.2)*0.017,
    0.125,
    0.1),
    0.2+(-sp.y-0.5)*0.2, -0.35, -0.25,
    0.29 , 0.01); //keifer ein bisschen mehr eng war hilfreich
    float keifer = k;




    //keifer
    sp = p;
    k = knochen(p, vec3(0., -0.67+keiferHohe, -0.35), vec3(0.005, 0.02, 0.16),
    -0.3, -0.5, 0., 0.18+cos(sp.z*8.+3.14)*0.02, 0.045);
    keifer = smin(keifer, k, 0.04);

    //wkk schneid
    sp = p;
    k = knochen(p, vec3(0., -0.58+keiferHohe, -0.28 - cos(abs(sp.y)*12. + 3.)*0.06),
    vec3(0.001, 0.15, 0.055),
    0.2+(-sp.y-0.5)*0.2, -0.45, -0.3, 0.34, 0.01);
    keifer = smax(keifer, -k, 0.04);

    //wkk loch
    sp = p;
    sp.x = abs(sp.x) - 0.31;
    k = length(sp - vec3(0., -0.38+keiferHohe, -0.23)) - 0.06;
    keifer = smax(keifer, -k, 0.04);


    //kinn
    sp = p;
    k = knochen(p, vec3(0., -0.73+keiferHohe, -0.45-cos(sp.x*8.)*0.1), vec3(0.09, 0.02, 0.015),
    0., 0., 0., 0., 0.03);
    keifer = smin(keifer, k, 0.09);


    //sollte siemlich schaf sein
    //augen loche
    sp = p;
    sp.x = abs(sp.x) - 0.172;
    k = length(sp - vec3(0., -0.14, -0.48)) - 0.084;
    alle = smax(alle, -k, 0.075);

    //nase
    sp = p;
    k = knochen(p, vec3(0., -0.23, -0.51), vec3(0.03, 0.09, 0.08),
    -0.3, 0., 0.3, 0., 0.039);
    alle = smin(alle, k, 0.06);

    //nase scheiden
    sp = p;
    k = knochen(p, vec3(0., -0.25, -0.54), vec3(0.022, 0.08, 0.07),
    -0.35, 0., 0.3, 0., 0.03);
    alle = smax(alle, -k, 0.04);

    //nase linie
    sp = p;
    k = knochen(p, vec3(0., -0.25, -0.5), vec3(0.001, 0.12, 0.07),
    -0.35, 0., 0., 0., 0.005);
    alle = smin(alle, k, 0.03);


    float zahnNum = 12.;
    float th = 0.15;
    sp = p;
    cs = cos(th), si = sin(th);

    sp = sp - vec3(0., -0.43, -0.4);
    sp.yz*=mat2(cs, si, -si, cs);

    //cyl oben

    k = sdCappedCylinder(sp, 0.17 //nicht so breit
    + cos(sp.y*12. -5.)*0.015
    + abs(cos(atan(sp.z,sp.x)*zahnNum - 0.8))*0.005
    , 0.045 - abs(cos(atan(sp.z,sp.x)*zahnNum - 0.8))*0.02
    - cos(sp.z*6.-1.75)*0.05
    );
    alle = smin(alle, k, 0.06); //weniger smooth weil die echte schadel


    //return alle;

    //cyl unten
    sp = p - vec3(0., -0.65+keiferHohe, -0.42);
    k = sdCappedCylinder(sp, 0.15
    + cos(sp.y*12. - 1.5)*0.03
    + abs(cos(atan(sp.z,sp.x)*zahnNum - 0.8))*0.01
    , 0.05 - abs(cos(atan(sp.z,sp.x)*zahnNum - 0.8))*0.01);
    keifer = smin(keifer, k, 0.06);





    //oben zhan
    sp = p - vec3(0.0, -0.51, -0.41);
    cs = cos(0.33), si = sin(0.33);//erste versuch!!!!
    sp.xz*=mat2(cs, si, -si, cs);
    k = sdCircleCircle(sp*6.6, 24, 1.); //nicht so breit
    float zahn = k;

    //unten zahn
    sp = p - vec3(0.0, -0.58+keiferHohe, -0.39);
    cs = cos(0.33), si = sin(0.33);//erste versuch!!!!
    sp.xz*=mat2(cs, si, -si, cs);

    k = sdCircleCircle(sp*6.6, 24, -1.);//nicht so breit
    zahn = min(zahn, k);

    objID = vec4(zahn-0.05, alle, keifer, 0);
    alle = smin(zahn,alle, 0.09);
    alle = smin(alle, keifer, 0.09);

    //cyl schneiden
    sp = p - vec3(0., -0.58, -0.15);
    k = sdCappedCylinder(sp, 0.17, 0.24);
    alle = smax(alle, -k, 0.06);

    //schonheit
    sp = p;
    sp.x = abs(sp.x) - 0.18;
    k = length(sp - vec3(0., -0.32, -0.522)) - 0.012;
    alle = smax(alle, -k, 0.01);

    return alle;
}
float trace(vec3 ro, vec3 rd){

    float t = 0., d;

    for (int i = 0; i < 96; i++){

        d = map(ro + rd*t);

        // Using the hacky "abs," trick, for more accuracy.
        if(abs(d)<.001 || t>FAR) break;

        t += d*.75;  // Using more accuracy, in the first pass.
    }

    return t;
}


//thanks to Shane
float softShadow(vec3 ro, vec3 lp, float k){

    const int maxIterationsShad = 24;

    vec3 rd = lp - ro;

    float shade = 1.;
    float dist = .002;
    float end = max(length(rd), .001);
    float stepDist = end/float(maxIterationsShad);

    rd /= end;

    // Max shadow iterations - More iterations make nicer shadows, but slow things down. Obviously, the lowest
    // number to give a decent shadow is the best one to choose.
    for (int i = 0; i<maxIterationsShad; i++){

        float h = map(ro + rd*dist);
        //shade = min(shade, k*h/dist);
        shade = min(shade, smoothstep(0., 1., k*h/dist)); // Subtle difference. Thanks to IQ for this tidbit.
        // So many options here, and none are perfect: dist += min(h, .2), dist += clamp(h, .01, .2),
        // clamp(h, .02, stepDist*2.), etc.
        dist += clamp(h, .02, .25);

        // Early exits from accumulative distance function calls tend to be a good thing.
        if (h<0. || dist>end) break;
        //if (h<.001 || dist > end) break; // If you're prepared to put up with more artifacts.
    }

    // I've added 0.5 to the final shade value, which lightens the shadow a bit. It's a preference thing.
    // Really dark shadows look too brutal to me.
    return min(max(shade, 0.) + .25, 1.);
}

//Rudimentary getNormal function thanks to Shane
//vec3 getNormal(in vec3 p) {
//	const vec2 e = vec2(.001, 0);
//	return normalize(vec3(map(p + e.xyy) - map(p - e.xyy), map(p + e.yxy) - map(p - e.yxy),	map(p + e.yyx) - map(p - e.yyx)));
//}

//I think this is the tetrehedral normal or something like that
//to reduce compile time, thanks to iq
//vec3 getNormal(in vec3 p)
//{
//    vec2 e = vec2(1.0,-1.0)*0.5773*0.001;
//    return normalize( e.xyy*map( p + e.xyy ) +
//					  e.yyx*map( p + e.yyx ) +
//					  e.yxy*map( p + e.yxy ) +
//					  e.xxx*map( p + e.xxx ) );
//}

//Another normal function to further reduce compile time to
//one second thanks to iq!
vec3 getNormal(vec3 p){
    vec2 t = vec2(0.001,0);
    return normalize(map(p) - vec3(
    map(p - t.xyy),
    map(p - t.yxy),
    map(p - t.yyx)
    ));
}

vec3 getObjectColor(vec3 p){

    p*=0.6;
    p.y-=0.2;

    // "floor(p)" is analogous to a unique ID - based on position.
    vec3 ip = floor(p);


    vec3 knochenFarber = vec3(237./255., 218./255., 201./255.)*1.5;
    vec3 schwarze = vec3(0.);
    vec3 rot = vec3(0.4, 0., 0.);
    // Color up the objects in a cubic checkered arrangement using a subtle version
    // of IQ's palette formula.

    vec3 col =  vec3(1.4, 0., 0.);//vec3(.9, .45, 1.5);

    col = vec3(2.);
    col = vec3(237./255., 218./255., 201./255.)*1.5;
    //col = vec3(0.03);///schwarz
    // Reverse the RGB channels on some of the objects, for a bit of variance.
    //if(fract(rnd*1183.5437 + .42)>.65) col = col.zyx;
    vec3 gold = vec3(205./255.,120./255.,15./255.)*1.5;
    vec3 silber = vec3(0.2,0.3, 0.4);
    //zahn
    //if(stripe > 0.){col = vec3(1.0-stripe*0.7)*1.5;}
    //keifer
    if(svObjID == 0.){col = vec3(2.);}//vec3(237./255., 218./255., 201./255.)*1.5;}
    return col;

}

//thanks to Shane
vec3 doColor(in vec3 sp, in vec3 rd, in vec3 sn, in vec3 lp, float t){

    vec3 ld = lp-sp; // Light direction vector.
    float lDist = max(length(ld), .001); // Light to surface distance.
    ld /= lDist; // Normalizing the light vector.

    // Attenuating the light, based on distance.
    float atten = 1. / (1. + lDist*.2 + lDist*lDist*.1);

    // Standard diffuse term.
    float diff = max(dot(sn, ld), 0.);
    // Standard specualr term.
    float spec = pow(max( dot( reflect(-ld, sn), -rd ), 0.), 5.);
    if(svObjID == 0.){spec = pow(spec*6.,3.);}
    // Coloring the object. You could set it to a single color, to
    // make things simpler, if you wanted.
    vec3 objCol = getObjectColor(sp);

    // Combining the above terms to produce the final scene color.
    vec3 sceneCol = (objCol*(diff + 0.15) + vec3(0.8, 0.8, 1.)*spec*(1.2-svObjID/3.)) * atten;


    // Fog factor -- based on the distance from the camera.
    float fogF = smoothstep(0., .95, t/FAR);
    //
    // Applying the background fog. Just black, in this case, but you could
    // render sky, etc, as well.

    sceneCol = mix(sceneCol, vec3(0.01), fogF);
    //sceneCol = mix(sceneCol, vec3(1.), 1.0-length(sp.xy)-0.5);

    // Return the color. Performed once every pass... of which there are
    // only two, in this particular instance.
    return sceneCol;

}
void mainImage( out vec4 fragColor, in vec2 fragCoord ){

    // Screen coordinates.
    vec2 uv = (fragCoord.xy - iResolution.xy*.5) / iResolution.y;

    // Unit direction ray.
    vec3 rd = normalize(vec3(uv, 1.));


    float cs = cos(iTime * .25), si = sin(iTime * .25);
    //rd.xy = mat2(cs, si, -si, cs)*rd.xy;
    //rd.xz = mat2(cs, si, -si, cs)*rd.xz;

    // Ray origin. Doubling as the surface position, in this particular example.
    // I hope that doesn't confuse anyone.
    vec3 ro = vec3(0., 0., -3.);

    // Light position. Set in the vicinity the ray origin.
    vec3 lp = ro + vec3(0., 2., -0.5);


    // FIRST PASS.

    float t = trace(ro, rd);

    //Speichern Save the object IDs after the first pass.

    svObjID = objID.x<objID.y? 0. : 1.;
    if(objID.z < objID.x && objID.z < objID.y) svObjID = 2.;

    oSvObjID = objID;

    //thanks to Shane
    // Advancing the ray origin, "ro," to the new hit point.
    ro += rd*t;

    //thanks to Shane
    // Retrieving the normal at the hit point.
    vec3 sn = getNormal(ro);

    //thanks to Shane
    vec3 sceneColor = doColor(ro, rd, sn, lp, t);
    float sh = softShadow(ro +  sn*.0015, lp, 16.);

    sceneColor *= sh;

    fragColor = vec4(sqrt(clamp(sceneColor, 0., 1.)), 1);
}
// --------[ Original ShaderToy ends here ]---------- //

void main(void)
{
    mainImage(gl_FragColor, gl_FragCoord.xy);
}